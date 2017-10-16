package com.codespair.kafka.navigator.kafkanavigatorbe.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Repository
public class BusMeta {

  private KafkaConfig kafkaConfig;
  private KafkaConsumer<String, JsonNode> kafkaConsumer;

  public BusMeta(KafkaConfig kafkaConfig) {
    this.kafkaConfig = kafkaConfig;
    log.info("Connections created with specified kafka hosts: {}");
  }

  public void initializeClient(List<String> hosts) {
    kafkaConfig.setHosts(hosts);
    kafkaConsumer = new KafkaConsumer<>(kafkaConfig.kafkaProps());
    log.info("connection established, metrics? =========>>>>>>>>>>>>>>>>>>>>>>>");

    Map<MetricName, ? extends Metric> kMetrics = kafkaConsumer.metrics();
    for(Map.Entry<MetricName, ? extends Metric> metric: kMetrics.entrySet()) {
      log.info("Metric: {}, value: {}", metric.getKey(), metric.getValue().value());
    }
  }
}
