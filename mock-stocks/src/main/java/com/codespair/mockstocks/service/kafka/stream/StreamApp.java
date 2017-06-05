package com.codespair.mockstocks.service.kafka.stream;

import com.codespair.mockstocks.model.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Slf4j
@Service
public class StreamApp {

    private KafkaStreams createStockQuoteStreamsInstance(String hosts) {
        log.info("loading kafka stream configuration");
        final JsonSerializer<StockQuote> jsonSerializer = new JsonSerializer<>();
        final JsonDeserializer<StockQuote> jsonDeserializer = new JsonDeserializer<>(StockQuote.class);
        final Serde<StockQuote> serde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        KStreamBuilder kStreamBuilder = new KStreamBuilder();
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "mock-stocks-example-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        KStream<StockQuote, StockQuote> stockQuoteRaw = kStreamBuilder.stream(serde, serde, "stockQuoteTopic");
        KStream<StockQuote, StockQuote> stockQuoteStreamed = stockQuoteRaw.through("stockQuotesStreamed");
        stockQuoteStreamed.print();
        return new KafkaStreams(kStreamBuilder, props);
    }

    @PostConstruct
    public void startStreaming() {
        log.info("trying to start streaming...");
        KafkaStreams streams = createStockQuoteStreamsInstance("localhost:9092");
        streams.start();
    }
}
