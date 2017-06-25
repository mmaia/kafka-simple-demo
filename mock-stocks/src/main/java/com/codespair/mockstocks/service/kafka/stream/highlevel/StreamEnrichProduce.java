package com.codespair.mockstocks.service.kafka.stream.highlevel;

import com.codespair.mockstocks.config.KafkaConfigProperties;
import com.codespair.mockstocks.model.StockDetail;
import com.codespair.mockstocks.model.StockQuote;
import com.codespair.mockstocks.service.kafka.spring.producer.SpringKafkaProducer;
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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
    private final SpringKafkaProducer kafkaProducer;

    @Autowired
    public StreamEnrichProduce(KafkaConfigProperties kafkaConfigProperties, StockExchangeMaps stockExchangeMaps,
                               SpringKafkaProducer kafkaProducer)  {
        this.config = kafkaConfigProperties;
        this.stockExchangeMaps = stockExchangeMaps;
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * Creates s KafkaStreams using the high level api
     * @param hosts where kafka is running
     * @return a KafkaStreams that is associated to the specified topic ans serializers(Serdes).
     */
    public KafkaStreams createStreamsInstance(String hosts) {
        log.info("loading kafka stream configuration");
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        KStreamBuilder kStreamBuilder = new KStreamBuilder();
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getStreamEnrichProduce().getId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);

        //stream from topic...
        KStream<String, JsonNode> stockQuoteRawStream = kStreamBuilder.stream(Serdes.String(), jsonSerde , config.getStockQuote().getTopic());

        Map<String, Map> exchanges = stockExchangeMaps.getExchanges();
        ObjectMapper objectMapper = new ObjectMapper();

        // - enrich stockquote with stockdetails before producing to new topic
        stockQuoteRawStream.foreach((key, jsonNode) -> {
            StockQuote stockQuote = null;
            StockDetail stockDetail;
            try {
                stockQuote = objectMapper.treeToValue(jsonNode, StockQuote.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            JsonNode exchangeNode = jsonNode.get("exchange");
            // get stockDetail that matches current quote being processed
            Map<String, StockDetail> stockDetailMap = exchanges.get(exchangeNode.toString().replace("\"", ""));
            stockDetail = stockDetailMap.get(key);
            stockQuote.setStockDetail(stockDetail);
            kafkaProducer.send(config.getStreamEnrichProduce().getTopic(), stockQuote);
        });

        return new KafkaStreams(kStreamBuilder, props);
    }

    @PostConstruct
    public void startStreaming() throws InterruptedException {
        log.info("trying to start streaming...");
        Thread.sleep(1000);
        streams = createStreamsInstance(config.getHost());
        streams.start();
    }

    @PreDestroy
    public void wrapUp() {
        streams.close();
    }
}
