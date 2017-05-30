package com.codespair.kafka.mockstocks;

import com.codespair.kafka.mockstocks.service.utils.StockExchangeMaps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
public class MockStocksApplication {

	@Autowired
	StockExchangeMaps stockExchangeMaps;

	@PostConstruct
	public void printRandomStockSymbols() {
		for (int i = 0; i < 500; i++) {
			String[] stock = stockExchangeMaps.randomStockSymbol();
			log.info("exchange: " + stock[0] + " , symbol: " + stock[1]);
		}
	}

	@SuppressWarnings("squid:S2095")
	public static void main(String[] args) {
		SpringApplication.run(MockStocksApplication.class, args);
	}
}
