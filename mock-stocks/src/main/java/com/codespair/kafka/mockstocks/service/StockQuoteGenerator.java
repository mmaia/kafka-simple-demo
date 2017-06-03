package com.codespair.kafka.mockstocks.service;

import com.codespair.kafka.mockstocks.model.StockQuote;
import com.codespair.kafka.mockstocks.service.utils.QuoteGenerationException;
import com.codespair.kafka.mockstocks.service.utils.StockExchangeMaps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
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

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;


    // TODO - very first working version, lot's to improve here
    @SuppressWarnings("squid:S2189") // avoid being marked by check for infinite loop from sonarqube
    @PostConstruct
    public void startQuoteGeneration() throws QuoteGenerationException {
        if(enabled) {
            log.info("Starting random quote generation in {} milliseconds, with interval: {} milliseconds between each quote",
                    delayToStartInMilliseconds, intervalMilliseconds);
            try {
                Thread.sleep(delayToStartInMilliseconds);
                while(true) {
                    StockQuote stockQuote = stockExchangeMaps.randomStockSymbol();
                    kafkaTemplate.send("stockQuoteTopic", stockQuote.toString());
                    Thread.sleep(intervalMilliseconds);
                }
            }
            catch(InterruptedException e) {
                e.printStackTrace();
                log.warn(e.getMessage());
                throw new QuoteGenerationException(e.getMessage());
            }

        }
    }
}
