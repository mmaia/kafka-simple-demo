package com.codespair.kafka.mockstocks;

import com.codespair.kafka.mockstocks.service.utils.CSVParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
public class MockStocksApplication {

	@Autowired
	private CSVParser csvParser;

	public static void main(String[] args) {
		SpringApplication.run(MockStocksApplication.class, args);
	}

	@PostConstruct
	public void executeParser() throws Exception {
		csvParser.readCSVAsBeans();
	}
}
