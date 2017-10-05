package com.codespair.mockstocks.service.utils;

import com.codespair.mockstocks.config.GeneratorConfigProperties;
import com.codespair.mockstocks.model.Exchange;
import com.codespair.mockstocks.model.StockDetail;
import com.codespair.mockstocks.model.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * This class loads on startup the CSV files available in the classpath and specified on the
 * configuration file directory under the following 2 application properties
 * generator.exchange.csv.path - directory relative to classpath where to find csv files
 * generator.exchange.csv.files - list of csv file names to load from the specified directory.
 */
@Slf4j
@Configuration
public class StockExchangeMaps {

    private Map<String, Map> exchanges;

    private List<String> exchangeNames;

    private CSVLoader csvLoader;

    private GeneratorConfigProperties generatorConfigProperties;

    public StockExchangeMaps(CSVLoader csvLoader, GeneratorConfigProperties generatorConfigProperties) {
        this.csvLoader = csvLoader;
        this.generatorConfigProperties = generatorConfigProperties;
        this.exchanges = new HashMap<>();
    }

    public void loadCSVs() {
        getCsvFileNames().forEach(exchange -> {
            exchanges.put(exchange, stockExchagesMap(exchange));
            log.info("csv mapped: " + exchange);
        });
        exchangeNames = new ArrayList<>(exchanges.keySet());
    }

    private Map<String, StockDetail> stockExchagesMap(String exchange) {
        return csvLoader.loadExchangeCSV(generatorConfigProperties.getExchangeCsv().getPath() + exchange + ".csv");
    }

    private List<String> getCsvFileNames() {
        return generatorConfigProperties.getExchangeCsv().getFiles();
    }

    /**
     * @return - a random exchange from the list of csvs loaded. Name of the csv file loaded is used as exchange name.
     */
    String randomExchange() {
        Random random = new Random();
        int whichExchange = random.nextInt(exchangeNames.size());
        return exchangeNames.get(whichExchange);
    }

    /**
     * @return stockquote picked randomically.
     * @see StockQuote
     */
    public StockQuote randomStockSymbol() {
        String exchange = randomExchange();
        Map<String, StockDetail> stockDetailMap = exchanges.get(exchange);
        List<String> symbols = new ArrayList<>(stockDetailMap.keySet());
        Random random = new Random();
        int whichSymbol = random.nextInt(symbols.size());
        return StockQuote.builder()
                .exchange(buildExchange(exchange))
                .symbol(symbols.get(whichSymbol))
                .build();
    }

    public Map<String, Map> getExchanges() {
        return exchanges;
    }

    // TODO - potential refactor, possibly move method to Exchange would be better?
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
