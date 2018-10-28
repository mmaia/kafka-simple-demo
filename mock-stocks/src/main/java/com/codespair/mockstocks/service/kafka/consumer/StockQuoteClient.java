package com.codespair.mockstocks.service.kafka.consumer;

import com.codespair.mockstocks.config.KafkaConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@DependsOn("stringJsonNodeClientConsumer")
@Slf4j
public class StockQuoteClient {

    final KafkaConfigProperties config;
    final StringJsonNodeClientConsumer client;
    final String topicName;

    public StockQuoteClient(KafkaConfigProperties kafkaConfigProperties,
                            StringJsonNodeClientConsumer stringJsonNodeClientConsumer) {
        this.config = kafkaConfigProperties;
        this.client = stringJsonNodeClientConsumer;
        topicName = config.getStockQuote().getTopic();
    }

    public void startConsumingStockQuotes() {
        client.configure(topicName + "-client", topicName);
        client.startConsumer();
    }
}
