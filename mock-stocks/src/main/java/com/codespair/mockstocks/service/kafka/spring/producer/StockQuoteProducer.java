package com.codespair.mockstocks.service.kafka.spring.producer;

import com.codespair.mockstocks.config.KafkaConfigProperties;
import com.codespair.mockstocks.model.StockQuote;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class StockQuoteProducer {

    private KafkaConfigProperties config;
    private Producer<String, JsonNode> kafkaProducer;

    public StockQuoteProducer(KafkaConfigProperties kafkaConfigProperties) {
        this.config = kafkaConfigProperties;
        this.createProducer();
    }

    public void createProducer() {
        kafkaProducer = new KafkaProducer<>(configure());
    }

    public void send(StockQuote stockQuote) {
        kafkaProducer.send();
    }

    private Properties configure() {
        Properties properties = new Properties();

        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        properties.put("bootstrap.servers", config.getHost());
        properties.put("client.id", config.getStockQuote());
        properties.put("key.serializer", Serdes.String());
        properties.put("value.serializer", jsonSerde);

        return properties;
    }
}
