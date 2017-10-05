package com.codespair.mockstocks.service.kafka.producer;

import com.codespair.mockstocks.config.GeneratorConfigProperties;
import com.codespair.mockstocks.config.KafkaConfigProperties;
import com.codespair.mockstocks.model.Exchange;
import com.codespair.mockstocks.model.StockQuote;
import com.codespair.mockstocks.service.utils.StockExchangeMaps;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@SpringBootTest
public class StockQuoteGeneratorTest {

    @MockBean
    GeneratorConfigProperties gcp;
    @MockBean
    KafkaConfigProperties kcp;
    @MockBean
    StockExchangeMaps sem;
    @MockBean
    StringJsonNodeClientProducer sjncp;
    StockQuoteGenerator stockQuoteGenerator;

    @Before
    public void setup() {
        stockQuoteGenerator = new StockQuoteGenerator(gcp, kcp, sem, sjncp);
    }

    @Test
    public void enrichTest() throws Exception {
        StockQuote stockQuote = newStockQuote();
        assertThat(stockQuote.getHigh(), nullValue());
        assertThat(stockQuote.getLastTrade(), nullValue());
        assertThat(stockQuote.getStockDetail(), nullValue());
        assertThat(stockQuote.getLow(), nullValue());
        assertThat(stockQuote.getSymbol(), notNullValue());
        assertThat(stockQuote.getExchange(), notNullValue());
        stockQuote = stockQuoteGenerator.enrich(newStockQuote());
        assertThat(stockQuote.getHigh(), notNullValue());
        assertThat(stockQuote.getLastTrade(), notNullValue());
        assertThat(stockQuote.getLow(), notNullValue());


    }

    private StockQuote newStockQuote() {
        return StockQuote.builder()
                .exchange(Exchange.AMEX)
                .symbol("ING")
                .build();
    }

}
