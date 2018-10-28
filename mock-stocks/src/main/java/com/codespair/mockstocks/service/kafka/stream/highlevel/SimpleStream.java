package com.codespair.mockstocks.service.kafka.stream.highlevel;

import com.codespair.mockstocks.config.GeneratorConfigProperties;
import com.codespair.mockstocks.config.KafkaConfigProperties;
import com.codespair.mockstocks.model.StockQuote;
import com.codespair.mockstocks.service.kafka.stream.JsonPojoDeserializer;
import com.codespair.mockstocks.service.kafka.stream.JsonPojoSerializer;
import com.codespair.mockstocks.service.kafka.stream.JsonSerde;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        //stream from topic...
        KStream<String, JsonNode> stockQuoteRawStream =
                streamsBuilder.stream(config.getStockQuote().getTopic());
        // stream unchanged message to new topic...
        stockQuoteRawStream
                .to(config.getSimpleStream().getTopic());
        final Topology topology = streamsBuilder.build();
        return new KafkaStreams(topology, configuration());
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
        result.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        result.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);
        return result;
    }

    @PreDestroy
    public void wrapUp() {
        streams.close();
    }
}
