package com.codespair.mockstocks.service.kafka.stream.highlevel;

import com.codespair.mockstocks.config.GeneratorConfigProperties;
import com.codespair.mockstocks.config.KafkaConfigProperties;
import com.codespair.mockstocks.service.kafka.stream.JsonSerde;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class CountBySymbolKTable {


    private final GeneratorConfigProperties generatorConfigProperties;
    private final KafkaConfigProperties kafkaConfigProperties;
    private final StreamsBuilder streamsBuilder;
    private ReadOnlyKeyValueStore view;

    private KafkaStreams streams;

    @Autowired
    public CountBySymbolKTable(KafkaConfigProperties config, GeneratorConfigProperties generatorConfigProperties) {
        this.kafkaConfigProperties = config;
        this.generatorConfigProperties = generatorConfigProperties;
        this.streamsBuilder = new StreamsBuilder();
    }

    private void createStreamsInstance(List<String> hosts) {
        log.info("about to start streaming for exchange stock quote filtering...");
        final Properties props = getProperties(hosts);

        JsonSerde<JsonNode> stockQuoteJsonSerde = new JsonSerde<>();
        stockQuoteJsonSerde.defaultConfig();

        final GlobalKTable<String, JsonNode> countsBySymbol = streamsBuilder
          .globalTable(kafkaConfigProperties.getStockQuote().getTopic(), Materialized.<String, JsonNode, KeyValueStore<Bytes, byte[]>>as(COUNT_BY_SYMBOL_STORE)
            .withKeySerde(Serdes.String())
            .withValueSerde(stockQuoteJsonSerde));
        final Topology topology = streamsBuilder.build();
        streams = new KafkaStreams(topology, props);
        streams.start();
        // we create a ktable and count by key, and giving a name for the state store which will be created(locally and in a kafka topic).

        final String queryableStoreName = countsBySymbol.queryableStoreName();
        view = streams.store(queryableStoreName, QueryableStoreTypes.keyValueStore());
    }

  /**
   * Dummy implementation to demonstrate it works as intended.
   */
  @Async
    public void printCurrentNetflixStored() {
        while(true) {
            log.info("got value for Netflix from ktable: {}", view.get("NFLX"));// add key here.
            try{
                Thread.sleep(5000);
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }
        }
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

    private static Serde<JsonNode> getJsonNodeSerde() {
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        return Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
    }

    public void startExchangeFilterStreaming() throws InterruptedException {
        log.info("trying to start streaming...");
        Thread.sleep(generatorConfigProperties.getStartDelayMilliseconds() + 1000);
        createStreamsInstance(kafkaConfigProperties.getHosts());
    }

    @PreDestroy
    public void wrapUp() {
        streams.close();
    }

    private static final String COUNT_BY_SYMBOL_STORE = "count-by-symbol-global-ktable";
}
