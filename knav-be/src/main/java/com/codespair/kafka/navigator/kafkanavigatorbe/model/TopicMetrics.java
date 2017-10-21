package com.codespair.kafka.navigator.kafkanavigatorbe.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class TopicMetrics {
  private double bytesInPerSec;
  private double bytesOutPerSec;
  private double bytesRejectPerSec;
  private double failedFetchRequestsPerSec;
  private double failedProduceRequestsPerSec;
  private double MessagesInPerSec;
  private double TotalFetchRequestsPerSec;
  private double totalProduceRequestsPerSec;
}
