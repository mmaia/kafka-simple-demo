package com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Needs a kafka server running with JMX port enabled on kafka:9992
 */
public class KafkaJMXIntegrationTest {

  private final static String KAFKA_JMX = "kafka:9992";
  KafkaJMX kafkaJMX = new KafkaJMX();

  @Before
  public void shouldConnectTest() {
    assertTrue(kafkaJMX.connect(KAFKA_JMX));
  }

  @Test
  public void shouldReturnBrokerId() {
    kafkaJMX.getBrokerId();
//    kafkaJMX.getTopicMetric();
//    Optional<Integer> brokerId = kafkaJMX.getBrokerId();
//    assertNotNull(brokerId.get());
  }

}
