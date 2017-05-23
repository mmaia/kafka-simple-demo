package com.codespair.kafka.mockstocks.service.utils;


import com.codespair.kafka.mockstocks.model.StockDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CSVParserTest {

    @Autowired
    private CSVParser csvParser;

    Map<String, StockDetail> stocksBySymbol;

    @Before
    public void loadTestCSV() throws Exception {
        stocksBySymbol = csvParser.loadExchangeCSV("/static/AMEX.csv");
    }

    @Test
    public void shouldLoad10Items() throws Exception {
        assertThat(stocksBySymbol.size(), is(equalTo(10)));
    }

    @Test
    public void shouldGetBySymbol() {
        assertThat(stocksBySymbol.get("ACU"), notNullValue());
        assertThat(stocksBySymbol.get("FAX"), isA(StockDetail.class));
    }

    @Test
    public void shouldReturnNullIfSymbolInvalid() {
        assertThat(stocksBySymbol.get("FAKE"), nullValue());
    }

    @After
    public void cleanup() {
        this.stocksBySymbol = null;
    }
}
