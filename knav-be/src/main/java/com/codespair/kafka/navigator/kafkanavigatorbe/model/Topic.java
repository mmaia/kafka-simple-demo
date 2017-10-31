package com.codespair.kafka.navigator.kafkanavigatorbe.model;

import lombok.Data;

import java.util.List;

@Data
public class Topic {
  private String name;
  private List<TopicMetric> topicMetricList;

  public void addMetric(TopicMetric topicMetric) {
    topicMetricList.add(topicMetric);
  }
}
