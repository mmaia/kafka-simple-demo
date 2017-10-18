package com.codespair.kafka.navigator.kafkanavigatorbe.kafka;

import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties
@Component
public class KafkaConfig {

  private List<String> hosts;

  Map<String, Object> kafkaProps() {
    Map<String, Object> consumerConfigProperties = new HashMap<>();
    consumerConfigProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getHosts());
    consumerConfigProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    consumerConfigProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
    // starts with the smallest offset record registered for now.
    consumerConfigProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return consumerConfigProperties;
  }

}

