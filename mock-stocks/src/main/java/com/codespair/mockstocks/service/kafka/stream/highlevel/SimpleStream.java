package com.codespair.mockstocks.service.kafka.stream.highlevel;

import com.codespair.mockstocks.service.utils.KafkaConfigProperties;
import com.codespair.mockstocks.service.utils.StockExchangeMaps;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.Properties;

/**
 * Shows the simplest possible kafka stream.
 */
@Slf4j
@Service
public class SimpleStream {

    private KafkaConfigProperties config;
    private KafkaStreams streams;
    private StockExchangeMaps stockExchangeMaps;

    @Autowired
    public SimpleStream(KafkaConfigProperties kafkaConfigProperties, StockExchangeMaps stockExchangeMaps) {
        this.config = kafkaConfigProperties;
        this.stockExchangeMaps = stockExchangeMaps;
    }

    /**
     * Creates s KafkaStreams using the high level api. Simplest possible implementation.
     * Create a stream from(when using defaults) stockQuotesTopic and send data direclty
     * to another topic called simpleStockQuoteStreamTopic without any modifications or
     * deserialization.
     * @param hosts where kafka is running
     * @return a KafkaStreams that is associated to the specified topic ans serializers(Serdes).
     */
    public KafkaStreams createStockQuoteStreamsInstance(String hosts) {
        log.info("loading kafka stream configuration");
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        KStreamBuilder kStreamBuilder = new KStreamBuilder();
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getStreamAppId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        //stream from topic...
        KStream<String, JsonNode> stockQuoteRawStream = kStreamBuilder.stream(Serdes.String(), jsonSerde , config.getStockQuoteTopic());
        // stream unchanged message to new topic...
        stockQuoteRawStream.to(Serdes.String(), jsonSerde, config.getStreamAppTopic());
        return new KafkaStreams(kStreamBuilder, props);
    }

    @PostConstruct
    public void startStreaming() throws InterruptedException {
        log.info("trying to start streaming...");
        Thread.sleep(config.getDelayToStartInMilliseconds() + 2000);
        streams = createStockQuoteStreamsInstance(config.getKafkaHost());
        streams.start();
    }

    @PreDestroy
    public void wrapUp() {
        streams.close();
    }
}