package com.codespair.kafka.mockstocks;

import com.codespair.kafka.mockstocks.model.StockQuote;
import com.codespair.kafka.mockstocks.service.utils.StockExchangeMaps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
public class MockStocksApplication {
	@SuppressWarnings("squid:S2095")
	public static void main(String[] args) {
		SpringApplication.run(MockStocksApplication.class, args);
	}
}
