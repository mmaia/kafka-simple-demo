package com.codespair.kafka.mockstocks.service.utils;

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

    CSVLoader csvLoader;

    public StockExchangeMaps(CSVLoader csvLoader) {
        this.csvLoader = csvLoader;
        this.exchanges = new HashMap<>();
    }

    @PostConstruct
    public void loadCsvs() {
        csvFilesToLoad.forEach((exchange) -> {
            exchanges.put(exchange, csvLoader.loadExchangeCSV(path + exchange + ".csv"));
            log.info("csv mapped: " + exchange);
        });
    }

    private Map<String, StockQuote> randomExchange() {
        Map<String, StockQuote> result;
        Random random = new Random();
        int exchage = random.nextInt(3) + 1;
        switch(exchage) {
            case 1:

                break;
            case 2:
                break;
            case 3:
                break;
            default:

        }
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
