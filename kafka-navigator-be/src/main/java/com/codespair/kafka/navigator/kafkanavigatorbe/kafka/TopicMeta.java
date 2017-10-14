package com.codespair.kafka.navigator.kafkanavigatorbe.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class collects metadata and metrics from topics of specified kafka hosts.
 */
@Slf4j
public class TopicMeta {

  private KafkaConsumer<String, JsonNode> kafkaConsumer;

  public TopicMeta(List<String> hosts) {
    kafkaConsumer = new KafkaConsumer<>(kafkaProps(hosts));
    log.info("Connections created with specified kafka hosts: {}", hosts);
  }

  /**
   * Recovers a map where keys are topic names and values are PartitionInfo objects with detailed
   * information of each partition.
   * @return a Map where each key is a topic and each value is a PartitionInfo object with all
   * required information.
   */
  public Map<String, List<PartitionInfo>> topicData() {
    Map<String, List<PartitionInfo>> topicInfo = kafkaConsumer.listTopics();
    for(Map.Entry<String, List<PartitionInfo>> item: topicInfo.entrySet()) {
      if (ignoreMeta(item)) {
        topicInfo.remove(item.getKey());
        continue;
      }
    }
    return topicInfo;
  }

  /**
   * DON'T RETURN metadata topics used from core kafka libraries like(__consumer_offsets)
   */
  private boolean ignoreMeta(Map.Entry<String, List<PartitionInfo>> item) {
    return item.getKey().startsWith("__");
  }

  private Map<String, Object> kafkaProps(List<String> hosts) {
    Map<String, Object> consumerConfigProperties = new HashMap<>();
    consumerConfigProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
    consumerConfigProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    consumerConfigProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
    // starts with the smallest offset record registered for now.
    consumerConfigProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return consumerConfigProperties;
  }
}
