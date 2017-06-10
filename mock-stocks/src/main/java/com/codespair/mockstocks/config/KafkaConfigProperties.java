package com.codespair.mockstocks.config;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfigProperties {

    @NotBlank
    private String host;

    @Data
    @Validated
    public static class StockQuote {
        private String topic;
    }

    @Data
    public static class SimpleStream {
        private String id;
        private String topic;
    }

    @Data
    public static class StreamChain {
        private String id;
        private String amexTopic;
        private String nyseTopic;
        private String nasdaqTopic;
    }

    @Data
    public static class StreamEnrichProduce {
        private String id;
        private String topic;
    }

    private StreamEnrichProduce streamEnrichProduce;
    private StreamChain streamChain;
    private SimpleStream simpleStream;
    private StockQuote stockQuote;

}
