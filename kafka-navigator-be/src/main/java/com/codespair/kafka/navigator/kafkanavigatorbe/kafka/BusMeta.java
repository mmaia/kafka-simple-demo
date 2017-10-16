package com.codespair.kafka.navigator.kafkanavigatorbe.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  /**
   * Establishes initial connection with kafka and get the metrics available
   * @param hosts the list of kafka hosts to connect with
   * @return Set where each element is a KMetric object with kafka metrics
   */
  public Set<KMetric> initializeClient(List<String> hosts) {
    Set<KMetric> result = new HashSet<>();
    kafkaConfig.setHosts(hosts);
    kafkaConsumer = new KafkaConsumer<>(kafkaConfig.kafkaProps());
    log.info("connection established, getting metrics");
    Map<MetricName, ? extends Metric> kMetrics = kafkaConsumer.metrics();
    for(Map.Entry<MetricName, ? extends Metric> metric: kMetrics.entrySet()) {
      KMetric kMetric = KMetric.builder()
          .groupName(metric.getKey().group())
          .description(metric.getKey().description())
          .name(metric.getKey().name())
          .tags(metric.getKey().tags())
          .value(metric.getValue().value()).build();
      result.add(kMetric);
    }
    return result;
  }
}
