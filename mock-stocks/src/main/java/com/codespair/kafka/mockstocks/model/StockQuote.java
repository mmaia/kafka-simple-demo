package com.codespair.kafka.mockstocks.model;

import java.math.BigDecimal;

public class StockQuote {
    private String symbol;
    private Exchange exchange;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal lastTrade;

    public enum Exchange {
        NYSE,
        NASDAQ,
        AMEX;
    }

}
