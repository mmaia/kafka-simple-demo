package com.codespair.kafka.mockstocks.service.utils;

import com.codespair.kafka.mockstocks.model.Exchange;
import com.codespair.kafka.mockstocks.model.StockDetail;
import com.codespair.kafka.mockstocks.model.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Configuration
public class StockExchangeMaps {

    @Value("${generator.exchange.csv.path}")
    private String path;

    @Value("#{'${generator.exchange.csv.files}'.split(',')}")
    private List<String> csvFilesToLoad;

    private Map<String, Map> exchanges;

    private List<String> exchangeNames;

    private CSVLoader csvLoader;

    public StockExchangeMaps(CSVLoader csvLoader) {
        this.csvLoader = csvLoader;
        this.exchanges = new HashMap<>();
    }

    @PostConstruct
    public void loadCSVs() {
        csvFilesToLoad.forEach(exchange -> {
            exchanges.put(exchange, csvLoader.loadExchangeCSV(path + exchange + ".csv"));
            log.info("csv mapped: " + exchange);
        });
        exchangeNames = new ArrayList<>(exchanges.keySet());
    }

    /**
     * @return - a random exchange from the list of csvs loaded. Name of the csv file loaded is used as exchange name.
     */
    public String randomExchange() {
        Random random = new Random();
        int whichExchange = random.nextInt(exchangeNames.size());
        return exchangeNames.get(whichExchange);
    }

    public StockQuote randomStockSymbol() {
        StockQuote result = new StockQuote();
        String exchange = randomExchange();
        result.setExchange(buildExchange(exchange));
        log.info("got exchange: " + exchange);
        Map<String, StockDetail> stockDetailMap = exchanges.get(exchange);
        List<String> symbols = new ArrayList<>(stockDetailMap.keySet());
        Random random = new Random();
        int whichSymbol = random.nextInt(symbols.size());
        result.setSymbol(symbols.get(whichSymbol));
        log.info("StockQuote randomly picked: " + result);
        return result;
    }

    private Exchange buildExchange(String exchange) {
        Exchange result = null;
        switch (exchange) {
            case "AMEX":
                result = Exchange.AMEX;
                break;
            case "NASDAQ":
                result =  Exchange.NASDAQ;
                break;
            case "NYSE":
                result = Exchange.NYSE;
                break;
            default:
                log.warn("Could not build exchange based on the input which was: {}", exchange);
                break;
        }
        return result;
    }
}
