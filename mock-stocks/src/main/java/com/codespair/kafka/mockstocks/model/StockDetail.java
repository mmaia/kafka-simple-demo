package com.codespair.kafka.mockstocks.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Year;

@Data
@Slf4j
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
