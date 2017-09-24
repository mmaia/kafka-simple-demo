package com.codespair.mockstocks.service.kafka.producer;

import com.codespair.mockstocks.config.KafkaConfigProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.Future;

@Slf4j
@Component
@Scope("prototype")
public class StringJsonNodeClientProducer {

    private KafkaConfigProperties config;
    private Producer<String, JsonNode> kafkaProducer;
    private String clientId;

    public StringJsonNodeClientProducer(KafkaConfigProperties kafkaConfigProperties) {
        this.config = kafkaConfigProperties;
    }

    public void initializeClient(String clientId) {
        this.clientId = clientId;
        createProducer();
    }

    private void createProducer() {
        kafkaProducer = new KafkaProducer<>(kafkaClientProperties());
    }

    public Future<RecordMetadata> send(String topic, String key, Object instance) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.convertValue(instance, JsonNode.class);
        return kafkaProducer.send(new ProducerRecord<>(topic, key,
                jsonNode));
    }

    public void close() {
        kafkaProducer.close();
    }

    private Properties kafkaClientProperties() {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getHosts());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return properties;
    }
}

