package com.codespair.mockstocks.service.kafka.stream.highlevel;

import com.codespair.mockstocks.config.GeneratorConfigProperties;
import com.codespair.mockstocks.config.KafkaConfigProperties;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Properties;

/**
 * Shows the simplest possible kafka stream.
 */
@Slf4j
@Service
public class SimpleStream {

    private GeneratorConfigProperties generatorConfigProperties;
    private KafkaConfigProperties config;
    private KafkaStreams streams;

    public SimpleStream(KafkaConfigProperties config,
                        GeneratorConfigProperties generatorConfigProperties) {
        this.config = config;
        this.generatorConfigProperties = generatorConfigProperties;
    }

    /**
     * Creates s KafkaStreams using the high level api. Simplest possible implementation.
     * Create a stream from a topic and send data direclty
     * to another topic without any modification.
     *
     * @return a KafkaStreams that is associated to the specified topic ans serializers(Serdes).
     */
    KafkaStreams createStreamsInstance() {
        log.info("loading kafka stream configuration");
        KStreamBuilder kStreamBuilder = new KStreamBuilder();
        //stream from topic...
        KStream<String, JsonNode> stockQuoteRawStream =
                kStreamBuilder.stream(
                    Serdes.String(),
                    jsonSerde(),
                    config.getStockQuote().getTopic());

        // stream unchanged message to new topic...
        stockQuoteRawStream
                .to(
                    Serdes.String(),
                    jsonSerde(),
                    config.getSimpleStream().getTopic());

        return new KafkaStreams(kStreamBuilder, configuration());
    }

    public void startStreaming() throws InterruptedException {
        log.info("trying to start streaming...");
        Thread.sleep(generatorConfigProperties.getStartDelayMilliseconds() + 1000);
        streams = createStreamsInstance();
        streams.start();
    }

    private Properties configuration() {
        Properties result = new Properties();
        result.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getSimpleStream().getId());
        result.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getHosts());
        return result;
    }

    private Serde<JsonNode> jsonSerde() {
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
        return jsonSerde;
    }

    @PreDestroy
    public void wrapUp() {
        streams.close();
    }
}
