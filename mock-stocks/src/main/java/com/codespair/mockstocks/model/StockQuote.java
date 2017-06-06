package com.codespair.mockstocks.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockQuote {
    private String symbol;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal lastTrade;
    private Exchange exchange;
    private StockDetail stockDetail;
}
