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
  private List<TopicMetric> topicMetricList;
  private List<String> jmxDomains;
  private String host;
  private Integer port;
  private Integer jmxPort;
  private OperatingSystem os;
  private String kafkaVersion;
}
