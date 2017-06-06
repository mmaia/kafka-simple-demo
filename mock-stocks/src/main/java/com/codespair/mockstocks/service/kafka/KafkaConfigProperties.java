package com.codespair.mockstocks.service.kafka;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * This class loads all configiration used by kafka software in this project
 */
@Component
@Data
public class KafkaConfigProperties {

    @Value("${generator.stockquotes.enabled:false}")
    private boolean enabled;

    @Value("${generator.stockquotes.start.delay.milliseconds:3000}")
    private int delayToStartInMilliseconds;

    @Value("${generator.stockquotes.interval.milliseconds: 5000}")
    private int intervalMilliseconds;

    @Value("${generator.stockquotes.topic: stockQuoteTopic}")
    private String stockQuoteTopic;
}