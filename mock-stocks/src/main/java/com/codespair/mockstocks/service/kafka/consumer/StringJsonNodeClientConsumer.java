package com.codespair.mockstocks.service.kafka.consumer;

import com.codespair.mockstocks.config.KafkaConfigProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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
import java.util.*;

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
        this.kafkaConsumer = new KafkaConsumer<>(loadConsumerConfigProperties());
    }

    private Map loadConsumerConfigProperties() {
        Map consumerConfigProperties = new HashMap<String, Object>();
        Deserializer<JsonNode> deserializer = new JsonDeserializer();
        consumerConfigProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getHosts());
        consumerConfigProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerConfigProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfigProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer.getClass().getName());
        // starts with the smallest offset record registered in the stream.
        consumerConfigProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return consumerConfigProperties;
    }

    @PreDestroy
    public void close() {
        kafkaConsumer.close();
    }
}
