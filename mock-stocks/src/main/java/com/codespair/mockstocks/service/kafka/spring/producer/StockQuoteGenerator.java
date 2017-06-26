package com.codespair.mockstocks.service.kafka.spring.producer;

import com.codespair.mockstocks.config.GeneratorConfigProperties;
import com.codespair.mockstocks.config.KafkaConfigProperties;
import com.codespair.mockstocks.model.StockQuote;
import com.codespair.mockstocks.service.utils.StockExchangeMaps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.Random;

/**
 * This class on startup try to start generating random stockquotes unless disabled(default) in the configuration
 */
@Slf4j
@Service
public class StockQuoteGenerator {

    @Autowired
    private GeneratorConfigProperties generatorConfigProperties;

    @Autowired
    private KafkaConfigProperties kafkaConfigProperties;

    @Autowired
    private StockExchangeMaps stockExchangeMaps;

    @Autowired
    private KafkaProducerStringJsonNodeClient kafkaProducerStringJsonNodeClient;

    @Async
    public void startGenerator() throws InterruptedException {
        if(generatorConfigProperties.isEnabled()) {
            kafkaProducerStringJsonNodeClient.configure(kafkaConfigProperties.getStockQuote().getTopic());
            log.info("Starting random quote generation in {} milliseconds, with interval: {} milliseconds between each quote",
                    generatorConfigProperties.getStartDelayMilliseconds(), generatorConfigProperties.getIntervalMilliseconds());
            try {
                Thread.sleep(generatorConfigProperties.getStartDelayMilliseconds());
                while(true) {
                    StockQuote stockQuote = stockExchangeMaps.randomStockSymbol();
                    stockQuote = enrich(stockQuote);
                    kafkaProducerStringJsonNodeClient.send(kafkaConfigProperties.getStockQuote().getTopic(), null, stockQuote);
                    Thread.sleep(generatorConfigProperties.getIntervalMilliseconds());
                }
            }
            catch(InterruptedException e) {
                log.warn(e.getMessage());
                throw e;
            }
        }
    }

    public StockQuote enrich(StockQuote stockQuote) {
        Random random = new Random();
        int upTo = 1000;
        stockQuote.setHigh(new BigDecimal(random.nextFloat() * upTo));
        stockQuote.setLow(new BigDecimal(random.nextFloat() * upTo));
        stockQuote.setLastTrade(new BigDecimal(random.nextFloat() * upTo));
        return stockQuote;
    }

    @PreDestroy
    public void closeProducer() {
        kafkaProducerStringJsonNodeClient.close();
    }
}
