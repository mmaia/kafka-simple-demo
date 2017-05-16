package com.codespair.kafka.mockstocks.model;

import java.math.BigDecimal;
import java.time.Year;

public class StockDetail {
    private String symbol;
    private String name;
    private Exchange exchange;
    private BigDecimal marketCap;
    private Year ipoYear;
    private String sector;
    private String industry;
    private String summaryQuote;
}
