package com.codespair.kafka.navigator.kafkanavigatorbe.model;

public enum TopicMetricAttributeType {
  BYTES_IN_PER_SEC("BytesInPerSec"),
  BYTES_OUT_PER_SEC("BytesOutPerSec"),
  BYTES_REJECTED_PER_SEC("BytesRejectedPerSec"),
  FAILED_FETCH_REQUESTS_PER_SEC("FailedFetchRequestsPerSec"),
  FAILED_PRODUCE_REQUESTS_PER_SEC("FailedProduceRequestsPerSec"),
  MESSAGES_IN_PER_SEC("MessagesInPerSec"),
  TOTAL_FETCH_REQUESTS_PER_SEC("TotalFetchRequestsPerSec"),
  TOTAL_PRODUCE_REQUESTS_PER_SEC("TotalProduceRequestsPerSec");

  private final String attributeType;

  TopicMetricAttributeType(final String attributeType) {
    this.attributeType = attributeType;
  }

  public static TopicMetricAttributeType fromString(String attributeType) {
    for(TopicMetricAttributeType tmat: TopicMetricAttributeType.values()) {
      if(tmat.toString().equalsIgnoreCase(attributeType)) {
        return tmat;
      }
    }
    throw new IllegalArgumentException("There's not TopicMetricAttributeType that matches the string: " + attributeType);
  }

  @Override
  public String toString() {
    return attributeType;
  }

}