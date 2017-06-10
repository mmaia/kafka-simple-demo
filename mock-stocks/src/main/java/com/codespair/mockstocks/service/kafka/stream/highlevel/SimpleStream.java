package com.codespair.mockstocks.service.kafka.stream.highlevel;

import com.codespair.mockstocks.config.GeneratorConfigProperties;
import com.codespair.mockstocks.config.KafkaConfigProperties;
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

    private GeneratorConfigProperties generatorConfigProperties;
    private KafkaConfigProperties kafkaConfigProperties;
    private KafkaStreams streams;

    @Autowired
    public SimpleStream(KafkaConfigProperties kafkaConfigProperties,
                        GeneratorConfigProperties generatorConfigProperties) {
        this.kafkaConfigProperties = kafkaConfigProperties;
        this.generatorConfigProperties = generatorConfigProperties;
    }

    /**
     * Creates s KafkaStreams using the high level api. Simplest possible implementation.
     * Create a stream from(when using defaults) stockQuotesTopic and send data direclty
     * to another topic called simpleStockQuoteStreamTopic without any modifications or
     * deserialization.
     * @param hosts where kafka is running
     * @return a KafkaStreams that is associated to the specified topic ans serializers(Serdes).
     */
    private KafkaStreams createStreamsInstance(String hosts) {
        log.info("loading kafka stream configuration");
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        KStreamBuilder kStreamBuilder = new KStreamBuilder();
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaConfigProperties.getSimpleStream().getId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        //stream from topic...
        KStream<String, JsonNode> stockQuoteRawStream = kStreamBuilder.stream(Serdes.String(), jsonSerde , kafkaConfigProperties.getStockQuote().getTopic());
        // stream unchanged message to new topic...
        stockQuoteRawStream.to(Serdes.String(), jsonSerde, kafkaConfigProperties.getSimpleStream().getTopic());
        return new KafkaStreams(kStreamBuilder, props);
    }

    @PostConstruct
    public void startStreaming() throws InterruptedException {
        log.info("trying to start streaming...");
        Thread.sleep(generatorConfigProperties.getStartDelayMilliseconds() + 1000);
        streams = createStreamsInstance(kafkaConfigProperties.getHost());
        streams.start();
    }

    @PreDestroy
    public void wrapUp() {
        streams.close();
    }
}
