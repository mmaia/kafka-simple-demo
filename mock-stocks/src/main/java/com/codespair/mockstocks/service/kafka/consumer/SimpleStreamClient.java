package com.codespair.mockstocks.service.kafka.consumer;

import com.codespair.mockstocks.config.KafkaConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@DependsOn("stringJsonNodeClientConsumer")
@Slf4j
public class SimpleStreamClient {
    KafkaConfigProperties config;
    StringJsonNodeClientConsumer client;

    public SimpleStreamClient(KafkaConfigProperties kafkaConfigProperties, StringJsonNodeClientConsumer stringJsonNodeClientConsumer) {
        this.config = kafkaConfigProperties;
        this.client = stringJsonNodeClientConsumer;
    }

    @PostConstruct
    public void consumeStockQuotes() {
        client.configure(config.getSimpleStream().getTopic() + "-client", config.getSimpleStream().getTopic());
        client.startConsumer();
    }
}
