package com.codespair.mockstocks.service.utils;

import lombok.Data;
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
    private boolean enabled;

    @Value("${generator.exchange.csv.path:/static/}")
    private String path;

    @Value("#{'${generator.exchange.csv.files:AMEX,NYSE,NASDAQ}'.split(',')}")
    private List<String> csvFilesToLoad;

    @Value("${generator.start.delay.milliseconds:3000}")
    private int delayToStartInMilliseconds;

    @Value("${generator.interval.milliseconds: 5000}")
    private int intervalMilliseconds;


    // kafka config
    @Value("${kafka.host}")
    private String kafkaHost;

    @Value("${kafka.stock-quote.topic}")
    private String stockQuoteTopic;

    @Value("${kafka.simple-stream.id}")
    private String streamAppId;

    @Value("${kafka.simple-stream.topic}")
    private String streamAppTopic;

    @Value("${kafka.stream-enrich-produce.id}")
    private String streamEnrichProduceAppId;

    @Value("${kafka.stream-enrich-produce.topic}")
    private String streamAppEnrichProduceTopic;
}
