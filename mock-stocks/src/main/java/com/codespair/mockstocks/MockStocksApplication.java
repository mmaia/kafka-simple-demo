package com.codespair.mockstocks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@SpringBootApplication
@EnableAsync
public class MockStocksApplication {

    protected MockStocksApplication() {
    }

    /**
     * Thread pool to handle Async operations
     * @return the thread pool to be used for async and scheduled processes in the application.
     * @see <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html">Spring Task Execution and Scheduling</a>
     */
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
}
