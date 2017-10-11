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
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Properties;

@Service
@DependsOn("streamChain")
@Slf4j
public class CountBySymbolKTable {


    private final GeneratorConfigProperties generatorConfigProperties;
    private final KafkaConfigProperties kafkaConfigProperties;
    private KafkaStreams streams;

    @Autowired
    public CountBySymbolKTable(KafkaConfigProperties config, GeneratorConfigProperties generatorConfigProperties) {
        this.kafkaConfigProperties = config;
        this.generatorConfigProperties = generatorConfigProperties;
    }

    private KafkaStreams createStreamsInstance(List<String> hosts) {
        log.info("about to start streaming for exchange stock quote filtering...");
        final Serde<JsonNode> jsonSerde = getJsonNodeSerde();
        final Properties props = getProperties(hosts);
        KStreamBuilder kStreamBuilder = new KStreamBuilder();

        //stream from amex topic...
        KStream<String, JsonNode> amexStream = kStreamBuilder.stream(Serdes.String(), jsonSerde, kafkaConfigProperties.getStreamChain().getAmexTopic());
        // we create a ktable and count by key, and giving a name for the state store which will be created(locally and in a kafka topic).
        KTable<String, Long> countsBySymbol = amexStream.groupByKey(Serdes.String(), jsonSerde).count("amex-count-by-symbol");
        return new KafkaStreams(kStreamBuilder, props);
    }

    private Properties getProperties(List<String> hosts) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "count-by-symbol-ktable");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
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
        streams = createStreamsInstance(kafkaConfigProperties.getHosts());
        streams.start();
    }

    @PreDestroy
    public void wrapUp() {
        streams.close();
    }
}
