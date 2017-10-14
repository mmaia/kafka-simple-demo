package com.codespair.mockstocks.service.kafka.stream.highlevel;

import com.codespair.mockstocks.config.KafkaConfigProperties;
import com.codespair.mockstocks.model.StockDetail;
import com.codespair.mockstocks.model.StockQuote;
import com.codespair.mockstocks.service.kafka.producer.StringJsonNodeClientProducer;
import com.codespair.mockstocks.service.utils.StockExchangeMaps;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class shows a KStream from a topic with a forEach call that enrich messages and produces to a new topic.
 */
@Slf4j
@Service
public class StreamEnrichProduce {

  private final KafkaConfigProperties config;
  private KafkaStreams streams;
  private final StockExchangeMaps stockExchangeMaps;
  private final StringJsonNodeClientProducer kafkaProducer;
  private Map<String, Map> exchanges;

  @Autowired
  public StreamEnrichProduce(KafkaConfigProperties kafkaConfigProperties,
                             StockExchangeMaps stockExchangeMaps,
                             StringJsonNodeClientProducer kafkaProducer) {
    this.config = kafkaConfigProperties;
    this.stockExchangeMaps = stockExchangeMaps;
    this.kafkaProducer = kafkaProducer;
  }

  /**
   * Creates s KafkaStreams using the high level api
   *
   * @param hosts where kafka is running
   * @return a KafkaStreams that is associated to the specified topic ans serializers(Serdes).
   */
  public KafkaStreams createStreamsInstance(List<String> hosts) {
    log.info("loading kafka stream configuration");
    final Serde<JsonNode> jsonSerde = jsonSerde();
    final Properties props = props(hosts);
    exchanges = stockExchangeMaps.getExchanges();
    KStreamBuilder kStreamBuilder = new KStreamBuilder();

    //stream from topic...
    KStream<String, JsonNode> stockQuoteRawStream = kStreamBuilder.stream(
        Serdes.String(),
        jsonSerde,
        stockQuoteTopic());

    kafkaProducer.initializeClient(streamEnrichProduceTopic() + "_id");

    // - enrich stockquote with stockdetails before producing to new topic
    stockQuoteRawStream.foreach((key, jsonNode) -> {
      StockQuote stockQuote = enrichStockQuote(key, jsonNode);
      if (stockQuote == null) return; // skip this item of stream
      kafkaProducer.send(
          streamEnrichProduceTopic(),
          stockQuote.getSymbol(),
          stockQuote);
    });

    return new KafkaStreams(kStreamBuilder, props);
  }

  private StockQuote enrichStockQuote(String key, JsonNode jsonNode) {
    StockQuote stockQuote = jsonToStockQuote(jsonNode);
    JsonNode exchangeNode = jsonNode.get("exchange");
    // get stockDetail that matches current quote being processed
    Map<String, StockDetail> stockDetailMap = exchanges.get(exchangeNode.toString().replace("\"", ""));
    StockDetail stockDetail = stockDetailMap.get(key);
    stockQuote.setStockDetail(stockDetail);
    return stockQuote;
  }

  private StockQuote jsonToStockQuote(JsonNode jsonNode) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.treeToValue(jsonNode, StockQuote.class);
    } catch (JsonProcessingException e) {
      log.error("Invalid message", e.getMessage(), e);
      return null;
    }
  }

  private Properties props(List<String> hosts) {
    Properties props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getStreamEnrichProduce().getId());
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
    return props;
  }

  private Serde<JsonNode> jsonSerde() {
    final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
    final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
    return Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
  }

  private String streamEnrichProduceTopic() {
    return config.getStreamEnrichProduce().getTopic();
  }

  private String stockQuoteTopic() {
    return config.getStockQuote().getTopic();
  }

  public void startStreaming() throws InterruptedException {
    log.info("trying to start streaming...");
    Thread.sleep(1000);
    streams = createStreamsInstance(config.getHosts());
    streams.start();
  }

  @PreDestroy
  public void wrapUp() {
    streams.close();
  }
}
