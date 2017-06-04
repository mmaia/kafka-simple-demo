package com.codespair.mockstocks.service.kafka.producer;

import com.codespair.mockstocks.model.StockQuote;
import com.codespair.mockstocks.service.utils.StockExchangeMaps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Random;

/**
 * This class on startup try to start generating random stockquotes unless disabled(default) in the configuration
 */
@Slf4j
@Service
public class StockQuoteGenerator {

    @Value("${generator.stockquotes.enabled:false}")
    private boolean enabled;

    @Value("${generator.stockquotes.start.delay.milliseconds:3000}")
    private int delayToStartInMilliseconds;

    @Value("${generator.stockquotes.interval.milliseconds: 5000}")
    private int intervalMilliseconds;


    @Value("${generator.stockquotes.topic: stockQuoteTopic}")
    private String stockQuoteTopic;


    @Autowired
    private StockExchangeMaps stockExchangeMaps;

    @Autowired
    KafkaProducer kafkaProducer;

    @SuppressWarnings("squid:S2189") // avoid being marked by check for infinite loop from sonarqube
    @PostConstruct
    public void startQuoteGeneration() throws InterruptedException {
        if(enabled) {
            log.info("Starting random quote generation in {} milliseconds, with interval: {} milliseconds between each quote",
                    delayToStartInMilliseconds, intervalMilliseconds);
            try {
                Thread.sleep(delayToStartInMilliseconds);
                while(true) {
                    StockQuote stockQuote = stockExchangeMaps.randomStockSymbol();
                    stockQuote = enrich(stockQuote);
                    kafkaProducer.send(stockQuoteTopic, stockQuote);
                    Thread.sleep(intervalMilliseconds);
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
