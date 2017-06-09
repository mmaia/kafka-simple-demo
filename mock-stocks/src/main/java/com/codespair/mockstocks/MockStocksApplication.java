package com.codespair.mockstocks;

import com.codespair.mockstocks.service.kafka.spring.producer.StockQuoteGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
@EnableAsync
public class MockStocksApplication {

	private StockQuoteGenerator stockQuoteGenerator;

	protected MockStocksApplication(StockQuoteGenerator stockQuoteGenerator) {
		this.stockQuoteGenerator = stockQuoteGenerator;
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(25);
		return executor;
	}

	@SuppressWarnings("squid:S2095")
	public static void main(String[] args) {
		SpringApplication.run(MockStocksApplication.class, args);
	}

	@SuppressWarnings("squid:S2189") // avoid being marked by check for infinite loop from sonarqube
	@PostConstruct
	public void startQuoteGeneration() {
		try {
			stockQuoteGenerator.startGenerator();
		} catch (InterruptedException e) {
			log.error("Error starting stock quotes generation", e.getMessage(), e);
		}
	}
}
