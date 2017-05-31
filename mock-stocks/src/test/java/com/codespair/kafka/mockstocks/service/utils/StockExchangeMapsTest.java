package com.codespair.kafka.mockstocks.service.utils;

import com.codespair.kafka.mockstocks.model.Exchange;
import com.codespair.kafka.mockstocks.model.StockDetail;
import com.codespair.kafka.mockstocks.model.StockQuote;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StockExchangeMapsTest {

    @Autowired
    private StockExchangeMaps stockExchangeMaps;

    @Autowired
    private CSVLoader csvLoader;

    private Map<String, StockDetail> stocksBySymbol;

    @Before
    public void loadTestCSV() {
        stocksBySymbol = csvLoader.loadExchangeCSV("/static/AMEX.csv");
    }

    @Test
    public void whenExchangeCSVOnlyAMEXRandomExchangeShouldReturnAMEX() {
        assertThat(stockExchangeMaps.randomExchange(), is("AMEX"));
    }

    @Test
    public void whenExchangeCSVOnlyAMEXRandomSymbolShouldReturn() {
        StockQuote stockQuote = stockExchangeMaps.randomStockSymbol();
        assertThat(stockQuote.getExchange(), is(Exchange.AMEX));
        assertThat(stockQuote.getSymbol(), isIn(stocksBySymbol.keySet()));
    }
}
