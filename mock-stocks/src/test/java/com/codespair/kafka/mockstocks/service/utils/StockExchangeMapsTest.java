package com.codespair.kafka.mockstocks.service.utils;

import com.codespair.kafka.mockstocks.model.StockDetail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
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
        String[] randomSymbol = stockExchangeMaps.randomStockSymbol();
        assertThat(randomSymbol[0], is("AMEX"));
        assertThat(randomSymbol[1], isIn(stocksBySymbol.keySet()));
    }
}
