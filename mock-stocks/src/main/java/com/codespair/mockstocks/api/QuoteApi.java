package com.codespair.mockstocks.api;

import com.codespair.mockstocks.model.StockQuote;
import com.codespair.mockstocks.service.kafka.stream.highlevel.QuoteBySymbolKTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/quotes")
public class QuoteApi {

  private final QuoteBySymbolKTable quoteBySymbolKTable;

  public QuoteApi(QuoteBySymbolKTable quoteBySymbolKTable) {

    this.quoteBySymbolKTable = quoteBySymbolKTable;
  }

  @GetMapping("/{symbol}")
  public ResponseEntity<StockQuote> findQuoteBySymbol(@PathVariable String symbol) {
    StockQuote stockQuote = quoteBySymbolKTable.quoteBySymbol(symbol);
    if(stockQuote == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(stockQuote);
  }
}
