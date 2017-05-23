package com.codespair.kafka.mockstocks.service.utils;

import com.codespair.kafka.mockstocks.model.StockQuote;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StockQuoteGenerator {

    CSVParser csvParser;

    public StockQuoteGenerator(CSVParser csvParser) {
        this.csvParser = csvParser;
    }

    @Bean("amex")
    public Map<String, StockQuote> amexExchange() throws Exception{
        return csvParser.loadExchangeCSV("/static/AMEX.csv");
    }

    @Bean("nasdaq")
    public Map<String, StockQuote> nasdaqExchange() throws Exception{
        return csvParser.loadExchangeCSV("/static/NASDAQ.csv");
    }

    @Bean("nyse")
    public Map<String, StockQuote> nyseExchange() throws Exception{
        return csvParser.loadExchangeCSV("/static/NYSE.csv");
    }

}
