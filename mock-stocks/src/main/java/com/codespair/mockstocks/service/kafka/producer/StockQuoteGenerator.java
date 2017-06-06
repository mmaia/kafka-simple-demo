package com.codespair.mockstocks.service.kafka.producer;

import com.codespair.mockstocks.model.StockQuote;
import com.codespair.mockstocks.service.kafka.KafkaConfigProperties;
import com.codespair.mockstocks.service.utils.StockExchangeMaps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * This class on startup try to start generating random stockquotes unless disabled(default) in the configuration
 */
@Slf4j
@Service
public class StockQuoteGenerator {

    @Autowired
    private KafkaConfigProperties kafkaConfigProperties;

    @Autowired
    private StockExchangeMaps stockExchangeMaps;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Async
    public void startGenerator() throws InterruptedException {
        if(kafkaConfigProperties.isEnabled()) {
            log.info("Starting random quote generation in {} milliseconds, with interval: {} milliseconds between each quote",
                    kafkaConfigProperties.getDelayToStartInMilliseconds(), kafkaConfigProperties.getIntervalMilliseconds());
            try {
                Thread.sleep(kafkaConfigProperties.getDelayToStartInMilliseconds());
                while(true) {
                    StockQuote stockQuote = stockExchangeMaps.randomStockSymbol();
                    stockQuote = enrich(stockQuote);
                    kafkaProducer.send(kafkaConfigProperties.getStockQuoteTopic(), stockQuote);
                    Thread.sleep(kafkaConfigProperties.getIntervalMilliseconds());
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
}
