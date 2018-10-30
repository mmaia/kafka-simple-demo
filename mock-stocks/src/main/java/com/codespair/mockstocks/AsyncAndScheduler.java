package com.codespair.mockstocks;

import com.codespair.mockstocks.service.kafka.consumer.SimpleStreamClient;
import com.codespair.mockstocks.service.kafka.consumer.StockQuoteClient;
import com.codespair.mockstocks.service.kafka.producer.StockQuoteGenerator;
import com.codespair.mockstocks.service.kafka.stream.highlevel.QuoteBySymbolKTable;
import com.codespair.mockstocks.service.kafka.stream.highlevel.SimpleStream;
import com.codespair.mockstocks.service.kafka.stream.highlevel.StreamChain;
import com.codespair.mockstocks.service.kafka.stream.highlevel.StreamEnrichProduce;
import com.codespair.mockstocks.service.utils.StockExchangeMaps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Manages services initialization and scheduling jobs if any.
 */
@Slf4j
@Component
public class AsyncAndScheduler implements ApplicationListener<ApplicationReadyEvent>{

    private StockExchangeMaps stockExchangeMaps;
    private StreamChain streamChain;
    private SimpleStreamClient simpleStreamClient;
    private StockQuoteGenerator stockQuoteGenerator;
    private StockQuoteClient stockQuoteClient;
    private StreamEnrichProduce streamEnrichProduce;
    private SimpleStream simpleStream;
    private QuoteBySymbolKTable quoteBySymbolKTable;

    public AsyncAndScheduler(StockExchangeMaps stockExchangeMaps,
                             StreamChain streamChain,
                             SimpleStreamClient simpleStreamClient,
                             StockQuoteGenerator stockQuoteGenerator,
                             StockQuoteClient stockQuoteClient,
                             StreamEnrichProduce streamEnrichProduce,
                             SimpleStream simpleStream,
                             QuoteBySymbolKTable quoteBySymbolKTable) {
        this.stockExchangeMaps = stockExchangeMaps;
        this.streamChain = streamChain;
        this.simpleStreamClient = simpleStreamClient;
        this.stockQuoteGenerator = stockQuoteGenerator;
        this.stockQuoteClient = stockQuoteClient;
        this.streamEnrichProduce = streamEnrichProduce;
        this.simpleStream = simpleStream;
        this.quoteBySymbolKTable = quoteBySymbolKTable;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
      log.info("starting kafka services, event: {}", event);
        try {
            stockExchangeMaps.loadCSVs();
            stockQuoteGenerator.startGenerator();
            stockQuoteClient.startConsumingStockQuotes();
            simpleStream.startStreaming();
            simpleStreamClient.startConsumingStockQuotes();
            streamEnrichProduce.startStreaming();
            streamChain.startExchangeFilterStreaming();
        } catch(Exception e) {
            log.error("Error starting kafka services: {}", e.getMessage(), e);
        }
    }
}
