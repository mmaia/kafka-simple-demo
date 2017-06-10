package com.codespair.mockstocks.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "generator")
@Data
public class GeneratorConfigProperties {
    private boolean enabled;
    private int startDelayMilliseconds;
    private int intervalMilliseconds;

    @Data
    public static class ExchangeCsv {
        private List<String> files;
        private String path;
    }

    private ExchangeCsv exchangeCsv;

}
