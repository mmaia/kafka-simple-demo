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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

/**
 * This class was just created to read the stream created from the ktable in CountBySymbolKTable
 * @author - mmaia on 6/25/17.
 */
@Slf4j
@Service
@DependsOn("countBySymbolKTable")
public class AmexCountBySymbolStream {

    private GeneratorConfigProperties generatorConfigProperties;
    private KafkaConfigProperties kafkaConfigProperties;
    private KafkaStreams streams;

    public AmexCountBySymbolStream (KafkaConfigProperties kafkaConfigProperties,
                        GeneratorConfigProperties generatorConfigProperties) {
        this.kafkaConfigProperties = kafkaConfigProperties;
        this.generatorConfigProperties = generatorConfigProperties;
    }

    private KafkaStreams createStreamsInstance(String hosts) {
        log.info("loading kafka stream configuration");

        KStreamBuilder kStreamBuilder = new KStreamBuilder();
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "amex-count-by-symbol-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        //stream from topic...
        KStream<String, Long> stockQuoteRawStream = kStreamBuilder.stream(Serdes.String(), Serdes.Long() , "amex-count-by-symbol");
        // print out messages...
        stockQuoteRawStream.print();
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
