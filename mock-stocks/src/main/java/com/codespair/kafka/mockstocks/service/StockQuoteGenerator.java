package com.codespair.kafka.mockstocks.service;

import com.codespair.kafka.mockstocks.model.StockQuote;
import com.codespair.kafka.mockstocks.service.utils.StockExchangeMaps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    @Autowired
    StockExchangeMaps stockExchangeMaps;

    @SuppressWarnings("squid:S2189") // avoid being marked by check for infinite loop from sonarqube
    @PostConstruct
    public void startQuoteGeneration() throws Exception{
        if(enabled) {
            log.info("Starting random quote generation in {} milliseconds, with interval: {} milliseconds between each quote",
                    delayToStartInMilliseconds, intervalMilliseconds);
            Thread.sleep(delayToStartInMilliseconds);

            while(true) {
                StockQuote stockQuote = stockExchangeMaps.randomStockSymbol();
                Thread.sleep(intervalMilliseconds);
            }

        }
    }
}
