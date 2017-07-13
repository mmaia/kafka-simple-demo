package com.codespair.mockstocks.service.kafka.consumer;

import com.codespair.mockstocks.config.KafkaConfigProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
@Scope("prototype")
@Slf4j
public class StringJsonNodeClientConsumer {

    private KafkaConfigProperties config;
    private KafkaConsumer<String, JsonNode> kafkaConsumer;
    private String groupId;
    private List<String> topicNames;

    public StringJsonNodeClientConsumer(KafkaConfigProperties kafkaConfigProperties) {
        this.config = kafkaConfigProperties;
        this.topicNames = new ArrayList<>();
    }

    public void configure(String groupId, String topicName) {
        this.groupId = groupId;
        this.topicNames.add(topicName);
        createConsumer();
    }

    @Async
    public void startConsumer() {
        kafkaConsumer.subscribe(this.topicNames);

        long counter = 0L;

        // we will start pooling for entries
        while(true) {
            ConsumerRecords<String, JsonNode> records = kafkaConsumer.poll(300);
            if(records.count() > 0) {
                for(ConsumerRecord<String, JsonNode> record: records) {
                    if(counter % 500 == 0) {
                        log.info("Record recovered, groupId: {}, topicName: {}, key: {}, value: {} , offset: {}",
                                this.groupId, this.topicNames, record.key(), record.value(), record.offset());
                    }
                    counter++;
                }
            }
        }
    }

    private void createConsumer() {
        this.kafkaConsumer = new KafkaConsumer<>(loadProperties());
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        Deserializer<JsonNode> deserializer = new JsonDeserializer();
        props.setProperty("bootstrap.servers", config.getHost());
        props.setProperty("group.id", groupId);
        props.setProperty("key.deserializer", StringDeserializer.class.getName());
        props.setProperty("value.deserializer", deserializer.getClass().getName());
        // starts with the smallest offset record registered in the stream.
        props.setProperty("auto.offset.reset", "earliest");
        return props;
    }

    @PreDestroy
    public void close() {
        kafkaConsumer.close();
    }
}
