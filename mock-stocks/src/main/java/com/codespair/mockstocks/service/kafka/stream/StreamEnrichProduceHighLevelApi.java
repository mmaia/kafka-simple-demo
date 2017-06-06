package com.codespair.mockstocks.service.kafka.stream;

import com.codespair.mockstocks.model.StockDetail;
import com.codespair.mockstocks.model.StockQuote;
import com.codespair.mockstocks.service.kafka.KafkaConfigProperties;
import com.codespair.mockstocks.service.utils.StockExchangeMaps;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;
import java.util.Properties;

@Slf4j
@Service
public class StreamEnrichProduceHighLevelApi {

    private KafkaConfigProperties config;
    private KafkaStreams streams;
    private StockExchangeMaps stockExchangeMaps;

    @Autowired
    public StreamEnrichProduceHighLevelApi(KafkaConfigProperties kafkaConfigProperties, StockExchangeMaps stockExchangeMaps) {
        this.config = kafkaConfigProperties;
        this.stockExchangeMaps = stockExchangeMaps;
    }

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getStreamAppId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        //stream from topic...
        KStream<String, JsonNode> stockQuoteRawStream = kStreamBuilder.stream(Serdes.String(), jsonSerde , config.getStockQuoteTopic());

        Map<String, Map> exchanges = stockExchangeMaps.getExchanges();
        log.info("exchanges: {} ", exchanges );
        ObjectMapper objectMapper = new ObjectMapper();

        // - enrich stockquote with stockdetails before streaming to new topic
        stockQuoteRawStream.foreach((key, jsonNode) -> {
            StockQuote stockQuote = null;
            StockDetail stockDetail;
            try {
                stockQuote = objectMapper.treeToValue(jsonNode, StockQuote.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            JsonNode exchangeNode = jsonNode.get("exchange");
            // get stockDetail that matches current quote being processed
            Map<String, StockDetail> stockDetailMap = exchanges.get(exchangeNode.toString().replace("\"", ""));
            stockDetail = stockDetailMap.get(key);
            stockQuote.setStockDetail(stockDetail);

            //TODO - jsonNode should be produced to a new topic with enriched data
            jsonNode = objectMapper.convertValue(stockQuote, JsonNode.class);
        });

        return new KafkaStreams(kStreamBuilder, props);
    }



    @PostConstruct
    public void startStreaming() throws InterruptedException {
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
