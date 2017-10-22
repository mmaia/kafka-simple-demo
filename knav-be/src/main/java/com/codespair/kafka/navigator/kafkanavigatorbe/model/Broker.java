package com.codespair.kafka.navigator.kafkanavigatorbe.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
public class Broker {
  private Integer id;
  private TopicMetrics topicMetrics;
  private List<String> allDomains;
}
