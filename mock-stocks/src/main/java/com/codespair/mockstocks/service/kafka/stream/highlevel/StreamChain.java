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
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
@DependsOn("streamEnrichProduce") // we need the topic to have data before starting this one...
public class StreamChain {

    private final GeneratorConfigProperties generatorConfigProperties;
    private final KafkaConfigProperties kafkaConfigProperties;
    private KafkaStreams streams;

    @Autowired
    public StreamChain(KafkaConfigProperties kafkaConfigProperties, GeneratorConfigProperties generatorConfigProperties)  {
        this.kafkaConfigProperties = kafkaConfigProperties;
        this.generatorConfigProperties = generatorConfigProperties;
    }

    /**
     * Creates KafkaStreams using the high level api.
     * Using default configuration streams from enrichedStockQuoteTopic applying a filter and sending the new
     * stream processed by the filter to a new topic containing only stocks quotes from AMEX exchange called amexTopic.
     * @param hosts where kafka is running.
     * @return a KafkaStreams that is associated to the specified topic and serializers(Serdes).
     */
    private
    KafkaStreams createStreamsInstance(List<String> hosts) {
        log.info("about to start streaming for exchange stock quote filtering...");
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        KStreamBuilder kStreamBuilder = new KStreamBuilder();
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaConfigProperties.getStreamChain().getId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        //stream from topic...
        KStream<String, JsonNode> stockQuoteRawStream = kStreamBuilder.stream(Serdes.String(), jsonSerde , kafkaConfigProperties.getStreamEnrichProduce().getTopic());

        // AMEX exchange stock quotes stream
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
        amexStockQuotes.to(Serdes.String(), jsonSerde, kafkaConfigProperties.getStreamChain().getAmexTopic());

        // NYSE exchange stock quotes stream
        KStream<String, JsonNode> nyseStockQuotes =
                stockQuoteRawStream
                        .filter((key, stockQuoteNode) -> {
                            boolean result = false;
                            JsonNode exchange = stockQuoteNode.get("exchange");
                            if(exchange.toString().replace("\"", "").equalsIgnoreCase("NYSE")) {
                                result = true;
                            }
                            return result;
                        });
        nyseStockQuotes.to(Serdes.String(), jsonSerde, kafkaConfigProperties.getStreamChain().getNyseTopic());

        // NASDAQ exchange stock quotes stream
        KStream<String, JsonNode> nasdaqStockQuotes =
                stockQuoteRawStream
                        .filter((key, stockQuoteNode) -> {
                            boolean result = false;
                            JsonNode exchange = stockQuoteNode.get("exchange");
                            if(exchange.toString().replace("\"", "").equalsIgnoreCase("NASDAQ")) {
                                result = true;
                            }
                            return result;
                        });
        nasdaqStockQuotes.to(Serdes.String(), jsonSerde, kafkaConfigProperties.getStreamChain().getNasdaqTopic());

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
