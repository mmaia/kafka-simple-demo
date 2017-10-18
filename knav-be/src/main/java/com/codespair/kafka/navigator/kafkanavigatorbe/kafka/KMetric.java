package com.codespair.kafka.navigator.kafkanavigatorbe.kafka;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class KMetric {
  private String name;
  private String groupName;
  private String description;
  private Map<String, String> tags;
  private double value;
}
