package com.codespair.kafka.mockstocks.service.utils;

import com.codespair.kafka.mockstocks.model.StockDetail;
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

    CSVLoader csvLoader;

    public StockExchangeMaps(CSVLoader csvLoader) {
        this.csvLoader = csvLoader;
        this.exchanges = new HashMap<>();
    }

    @PostConstruct
    public void loadCsvs() {
        csvFilesToLoad.forEach(exchange -> {
            exchanges.put(exchange, csvLoader.loadExchangeCSV(path + exchange + ".csv"));
            log.info("csv mapped: " + exchange);
        });
        exchangeNames = new ArrayList<>(exchanges.keySet());
    }

    public String randomExchange() {
        Random random = new Random();
        int whichExchange = random.nextInt(exchangeNames.size());
        return exchangeNames.get(whichExchange);
    }

    public String[] randomStockSymbol() {
        String[] result = new String[2];
        String exchange = randomExchange();
        result[0] = exchange;
        log.info("got exchange: " + exchange);
        Map<String, StockDetail> stockDetailMap = exchanges.get(exchange);
        List<String> symbols = new ArrayList<>(stockDetailMap.keySet());
        Random random = new Random();
        int whichSymbol = random.nextInt(symbols.size());
        result[1] = symbols.get(whichSymbol);
        return result;
    }
}
