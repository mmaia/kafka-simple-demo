package com.codespair.mockstocks.service.kafka.producer;

import com.codespair.mockstocks.config.KafkaConfigProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.Future;

@Component
@Scope("prototype")
public class StringJsonNodeClientProducer {

    private KafkaConfigProperties config;
    private Producer<String, JsonNode> kafkaProducer;
    private String clientId;

    public StringJsonNodeClientProducer(KafkaConfigProperties kafkaConfigProperties) {
        this.config = kafkaConfigProperties;
    }

    public void configure(String clientId) {
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
        kafkaProducer = null;
    }

    private Properties kafkaClientProperties() {
        Properties properties = new Properties();

        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();

        properties.put("bootstrap.servers", config.getHost());
        properties.put("client.id", clientId);
        properties.put("key.serializer", StringSerializer.class);
        properties.put("value.serializer", jsonSerializer.getClass());

        return properties;
    }
}

