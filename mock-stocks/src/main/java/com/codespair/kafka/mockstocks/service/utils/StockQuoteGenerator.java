package com.codespair.kafka.mockstocks.service.utils;

import com.codespair.kafka.mockstocks.model.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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
            log.info("Starting random quote generation in {} milliseconds, with interval: {}",
                    delayToStartInMilliseconds, intervalMilliseconds);
            Thread.sleep(delayToStartInMilliseconds);
            while(true) {
                StockQuote stockQuote = stockExchangeMaps.randomStockSymbol();
                log.info("exchange: " + stockQuote.getExchange() + " , symbol: " + stockQuote.getSymbol());
                Thread.sleep(intervalMilliseconds);
            }
        }
    }

}
