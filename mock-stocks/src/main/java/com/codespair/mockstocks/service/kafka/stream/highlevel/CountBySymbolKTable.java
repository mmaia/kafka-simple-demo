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

    private KafkaStreams createStreamsInstance(List<String> host) {
        log.info("about to start streaming for exchange stock quote filtering...");
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        KStreamBuilder kStreamBuilder = new KStreamBuilder();
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "count-by-symbol-ktable");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, host);

        //stream from topic...
        KStream<String, JsonNode> amexStream = kStreamBuilder.stream(Serdes.String(), jsonSerde, kafkaConfigProperties.getStreamChain().getAmexTopic());

        // we create a ktable and count by key, and giving a name for the state store which will be created by kafka
        KTable<String, Long> countsBySymbol = amexStream.groupByKey(Serdes.String(), jsonSerde).count("amex-count-by-symbol");

        log.debug("checking the just created local store name: {} ", countsBySymbol.getStoreName());

        // we then strem the table to a topic
        countsBySymbol.to(Serdes.String(), Serdes.Long(), "amex-count-by-symbol");

        return new KafkaStreams(kStreamBuilder, props);
    }

    @PostConstruct
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
