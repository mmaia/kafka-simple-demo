package com.codespair.mockstocks.service.kafka.stream;

import com.codespair.mockstocks.service.kafka.KafkaConfigProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.*;
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

@Slf4j
@Service
public class StreamHighLevelApi {

    @Autowired
    KafkaConfigProperties config;

    KafkaStreams streams;

    /**
     * Creates s KafkaStreams using the high level api
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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "mock-stocks-example-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        KStream<String, JsonNode> stockQuoteRawStream = kStreamBuilder.stream(Serdes.serdeFrom(String.class), jsonSerde , "stockQuoteTopic");

        // send message to new topic
        stockQuoteRawStream.to(Serdes.serdeFrom(String.class), jsonSerde,"stockQuoteStream");
        return new KafkaStreams(kStreamBuilder, props);
    }

    @PostConstruct
    public void startStreaming() throws InterruptedException {
        log.info("trying to start streaming...");
        Thread.sleep(config.getDelayToStartInMilliseconds() + 2000);
        streams = createStockQuoteStreamsInstance("localhost:9092");
        streams.start();
    }

    @PreDestroy
    public void wrapUp() {
        streams.close();
    }
}
