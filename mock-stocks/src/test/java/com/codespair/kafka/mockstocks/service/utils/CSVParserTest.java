package com.codespair.kafka.mockstocks.service.utils;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CSVParserTest {

    @Autowired
    private CSVParser csvParser;

    @Test
    public void loadExchangeCSVTest() throws Exception {
        Map<Integer, String> stocksBySymbol = csvParser.loadExchangeCSV("/static/AMEX.csv");
        assertThat(stocksBySymbol.size(), is(equalTo(10)));
    }
}
