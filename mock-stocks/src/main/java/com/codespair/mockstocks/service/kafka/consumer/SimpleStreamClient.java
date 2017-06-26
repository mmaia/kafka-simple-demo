package com.codespair.mockstocks.service.kafka.consumer;

import com.codespair.mockstocks.config.KafkaConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@DependsOn("kafkaConsumerStringJsonNodeClient")
@Slf4j
public class SimpleStreamClient {
    KafkaConfigProperties config;
    KafkaConsumerStringJsonNodeClient client;

    public SimpleStreamClient(KafkaConfigProperties kafkaConfigProperties, KafkaConsumerStringJsonNodeClient kafkaConsumerStringJsonNodeClient) {
        this.config = kafkaConfigProperties;
        this.client = kafkaConsumerStringJsonNodeClient;
    }

    @PostConstruct
    public void consumeStockQuotes() {
        client.configure(config.getSimpleStream().getTopic() + "-client", config.getSimpleStream().getTopic());
        client.startConsumer();
    }
}
