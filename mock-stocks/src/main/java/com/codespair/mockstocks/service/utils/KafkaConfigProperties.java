package com.codespair.mockstocks.service.utils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class loads all configiration used by kafka software in this project
 */
@Component
@Data
public class KafkaConfigProperties {

    // generator config
    @Value("${generator.enabled:false}")
    @Setter(AccessLevel.NONE)
    private boolean enabled;

    @Value("${generator.exchange.csv.path:/static/}")
    @Setter(AccessLevel.NONE)
    private String path;

    @Value("#{'${generator.exchange.csv.files:AMEX,NYSE,NASDAQ}'.split(',')}")
    @Setter(AccessLevel.NONE)
    private List<String> csvFilesToLoad;

    @Value("${generator.start.delay.milliseconds:1000}")
    @Setter(AccessLevel.NONE)
    private int delayToStartInMilliseconds;

    @Value("${generator.interval.milliseconds: 5000}")
    @Setter(AccessLevel.NONE)
    private int intervalMilliseconds;


    // kafka config
    @Value("${kafka.host:'localhost:9092'}")
    @Setter(AccessLevel.NONE)
    private String kafkaHost;

    @Value("${kafka.stock-quote.topic:stock-quote}")
    @Setter(AccessLevel.NONE)
    private String stockQuoteTopic;

    @Value("${kafka.simple-stream.id:simple-stream}")
    @Setter(AccessLevel.NONE)
    private String streamAppId;

    @Value("${kafka.simple-stream.topic:simple-stream}")
    @Setter(AccessLevel.NONE)
    private String streamAppTopic;

    @Value("${kafka.stream-enrich-produce.id:stream-enrich-produce}")
    @Setter(AccessLevel.NONE)
    private String streamEnrichProduceAppId;

    @Value("${kafka.stream-enrich-produce.topic:stream-enrich-produce}")
    @Setter(AccessLevel.NONE)
    private String streamAppEnrichProduceTopic;

    @Value("${kafka.stream-chain.topic.amex:amex-quotes}")
    @Setter(AccessLevel.NONE)
    private String amexQuotesTopic;

    @Value("${kafka.stream-chain.topic.nasdaq:nasdaq-quotes}")
    @Setter(AccessLevel.NONE)
    private String nasdaqQuotesTopic;

    @Value("${kafka.stream-chain.topic.nyse:nyse-quotes}")
    @Setter(AccessLevel.NONE)
    private String nyseQuotesTopic;

    @Value("${kafka.stream-chain.id:amex-quotes}")
    @Setter(AccessLevel.NONE)
    private String streamChainAppId;
}
