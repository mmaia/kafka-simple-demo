package com.codespair.mockstocks.model;

public enum Exchange {
    NYSE("NYSE"),
    NASDAQ("NASDAQ"),
    AMEX("AMEX");

    String exchange;

    Exchange(String exchange) {
        this.exchange = exchange;
    }
}
