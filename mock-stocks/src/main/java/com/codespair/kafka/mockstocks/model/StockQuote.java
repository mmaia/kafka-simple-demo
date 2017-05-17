package com.codespair.kafka.mockstocks.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockQuote {
    private String symbol;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal lastTrade;
    private StockDetail stockDetail;
}
