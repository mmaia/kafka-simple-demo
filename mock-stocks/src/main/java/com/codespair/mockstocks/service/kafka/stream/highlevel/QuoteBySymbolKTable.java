package com.codespair.mockstocks.service.kafka.stream.highlevel;

import com.codespair.mockstocks.config.GeneratorConfigProperties;
import com.codespair.mockstocks.config.KafkaConfigProperties;
import com.codespair.mockstocks.model.StockQuote;
import com.codespair.mockstocks.service.kafka.stream.JsonSerde;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class QuoteBySymbolKTable {

  private final GeneratorConfigProperties generatorConfigProperties;
  private final KafkaConfigProperties kafkaConfigProperties;
  private final StreamsBuilder streamsBuilder;
  private ReadOnlyKeyValueStore<String, StockQuote> view;

  private KafkaStreams streams;

  @Autowired
  public QuoteBySymbolKTable(KafkaConfigProperties config, GeneratorConfigProperties generatorConfigProperties) {
    this.kafkaConfigProperties = config;
    this.generatorConfigProperties = generatorConfigProperties;
    this.streamsBuilder = new StreamsBuilder();
  }

  private void createStreamsInstance(List<String> hosts) {
    log.info("about to start streaming for exchange stock quote filtering...");
    final Properties props = getProperties(hosts);

    JsonSerde<JsonNode> stockQuoteJsonSerde = new JsonSerde<>();
    stockQuoteJsonSerde.defaultConfig();

    final GlobalKTable<String, JsonNode> stockBySymbolGKTable = streamsBuilder
      .globalTable(kafkaConfigProperties.getStockQuote().getTopic(), Materialized.<String, JsonNode, KeyValueStore<Bytes, byte[]>>as(COUNT_BY_SYMBOL_STORE)
        .withKeySerde(Serdes.String())
        .withValueSerde(stockQuoteJsonSerde));
    final Topology topology = streamsBuilder.build();
    streams = new KafkaStreams(topology, props);
    streams.start();

    final String queryableStoreName = stockBySymbolGKTable.queryableStoreName();
    view = streams.store(queryableStoreName, QueryableStoreTypes.keyValueStore());
  }

  public StockQuote quoteBySymbol(String key) {
    return view.get(key);
  }

  private Properties getProperties(List<String> hosts) {
    Properties props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, COUNT_BY_SYMBOL_STORE);
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);
    props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp");
    return props;
  }

  @PostConstruct
  public void startExchangeFilterStreaming() throws InterruptedException {
    log.info("trying to start streaming for: {}", this.getClass().getCanonicalName());
    Thread.sleep(generatorConfigProperties.getStartDelayMilliseconds() + 1000);
    createStreamsInstance(kafkaConfigProperties.getHosts());
  }

  @PreDestroy
  public void wrapUp() {
    streams.close();
  }

  private static final String COUNT_BY_SYMBOL_STORE = "count-by-symbol-global-ktable";
}
