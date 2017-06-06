package com.codespair.mockstocks.service.kafka.stream;

import com.codespair.mockstocks.service.kafka.KafkaConfigProperties;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

@Service
@Slf4j
public class StreamChainHighLevelApi {
    private final KafkaConfigProperties config;
    private KafkaStreams streams;

    @Autowired
    public StreamChainHighLevelApi(KafkaConfigProperties kafkaConfigProperties)  {
        this.config = kafkaConfigProperties;
    }

    /**
     * Creates s KafkaStreams using the high level api
     * @param hosts where kafka is running
     * @return a KafkaStreams that is associated to the specified topic and serializers(Serdes).
     */
    @Async
    public KafkaStreams createStockQuoteStreamsInstance(String hosts) {
        log.info("about to start streaming for exchange stock quote filtering...");
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        KStreamBuilder kStreamBuilder = new KStreamBuilder();
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stock-quotes-streaming-exchange-filter");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        //stream from topic...
        KStream<String, JsonNode> stockQuoteRawStream = kStreamBuilder.stream(Serdes.String(), jsonSerde , config.getStreamAppEnrichProduceTopic());

        KStream<String, JsonNode> amexStockQuotes =
                stockQuoteRawStream
                        .filter((key, stockQuoteNode) -> {
                            boolean result = false;
                            JsonNode exchange = stockQuoteNode.get("exchange");
                            if(exchange.toString().replace("\"", "").equalsIgnoreCase("AMEX")) {
                                result = true;
                            }
                            return result;
                        });
        amexStockQuotes.to(Serdes.String(), jsonSerde, "amexTopic");

        return new KafkaStreams(kStreamBuilder, props);
    }

    @PostConstruct
    public void startExchangeFilterStreaming() throws InterruptedException {
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
