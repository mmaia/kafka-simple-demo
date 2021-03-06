package com.codespair.mockstocks.service.utils;


import com.codespair.mockstocks.model.StockDetail;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CSVLoaderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private CSVLoader csvLoader;

    private Map<String, StockDetail> stocksBySymbol;

    @Before
    public void loadTestCSV() {
        stocksBySymbol = csvLoader.loadExchangeCSV("/static/AMEX.csv");
    }

    @Test
    public void whenCSVContain10LinesThenMapSizeShouldBe10() throws Exception {
        assertThat(stocksBySymbol.size(), is(10));
    }

    @Test
    public void whenStockSymbolIsValidThenReturnStockDetail() {
        assertThat(stocksBySymbol.get("ACU"), notNullValue());
        assertThat(stocksBySymbol.get("FAX"), isA(StockDetail.class));
    }

    @Test
    public void whenStockSymbolInvalidThenReturnNull() {
        assertThat(stocksBySymbol.get("FAKE"), nullValue());
    }

    @Test
    public void whenFileToLoadNotFoundThenRuntimeException() {
        exception.expect(InvalidCSVPathException.class);
        csvLoader.loadExchangeCSV("invalid/path.csv");
    }

    @After
    public void cleanup() {
        this.stocksBySymbol = null;
    }
}
