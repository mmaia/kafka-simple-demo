package com.codespair.kafka.navigator.kafkanavigatorbe.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
@EqualsAndHashCode
public class KMetric {
  private String name;
  private String groupName;
  private String description;
  private Map<String, String> tags;
  private double value;
}