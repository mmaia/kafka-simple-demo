package com.codespair.mockstocks.service.kafka.consumer;

import com.codespair.mockstocks.config.KafkaConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("stringJsonNodeClientConsumer")
@Slf4j
public class SimpleStreamClient {
    KafkaConfigProperties config;
    StringJsonNodeClientConsumer client;

    public SimpleStreamClient(KafkaConfigProperties kafkaConfigProperties,
                              StringJsonNodeClientConsumer stringJsonNodeClientConsumer) {
        this.config = kafkaConfigProperties;
        this.client = stringJsonNodeClientConsumer;
    }

    public void startConsumingStockQuotes() {
        log.info("SimpleStreamClient.startconsumingStockQuotes");
        client.configure(topicName() + "-client", topicName());
        client.startConsumer();
    }

    private String topicName() {
        // streams from stock-quote
        return config.getStockQuote().getTopic();
    }
}
