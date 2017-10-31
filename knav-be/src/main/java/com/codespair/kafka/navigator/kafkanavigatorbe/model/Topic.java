package com.codespair.kafka.navigator.kafkanavigatorbe.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Topic {
  private String name;
  private List<TopicMetric> topicMetricList = new ArrayList<>();

  public void addMetric(TopicMetric topicMetric) {
    topicMetricList.add(topicMetric);
  }
}
