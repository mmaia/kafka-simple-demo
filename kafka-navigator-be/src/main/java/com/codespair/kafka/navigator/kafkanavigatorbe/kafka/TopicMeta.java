package com.codespair.kafka.navigator.kafkanavigatorbe.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class collects metadata and metrics from topics of specified kafka hosts.
 */
@Slf4j
@Repository
public class TopicMeta {

  private BusMeta busMeta;

  public TopicMeta(BusMeta busMeta) {
    this.busMeta = busMeta;
  }

  /**
   * Recovers a map where keys are topic names and values are PartitionInfo objects with detailed
   * information of each partition.
   * @return a Map where each key is a topic and each value is a PartitionInfo object with all
   * required information.
   */
  public Map<String, List<String>> topicData() {
    Map<String, List<String>> result = new HashMap<>();
    Map<String, List<PartitionInfo>> topicInfo = busMeta.getKafkaConsumer().listTopics();
    for(Map.Entry<String, List<PartitionInfo>> item: topicInfo.entrySet()) {
      if (ignoreMeta(item)) continue;
      List<String> partitionInfoString = new ArrayList<>();
      for (PartitionInfo pi: item.getValue()) {
        partitionInfoString.add(pi.toString());
      }
      result.put(item.getKey(), partitionInfoString);
    }
    return result;
  }

  /**
   * DON'T RETURN metadata topics used from core kafka libraries like(__consumer_offsets)
   */
  private boolean ignoreMeta(Map.Entry<String, List<PartitionInfo>> item) {
    return item.getKey().startsWith("__");
  }
}
