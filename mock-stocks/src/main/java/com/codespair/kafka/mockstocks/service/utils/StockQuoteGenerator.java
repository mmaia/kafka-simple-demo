package com.codespair.kafka.mockstocks.service.utils;

import org.springframework.stereotype.Service;

@Service
public class StockQuoteGenerator {

    @SuppressWarnings("squid:S2189") // avoid being marked by check for infinite loop from sonarqube
    public void startQuoteGeneration() {
        while(true) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

}
