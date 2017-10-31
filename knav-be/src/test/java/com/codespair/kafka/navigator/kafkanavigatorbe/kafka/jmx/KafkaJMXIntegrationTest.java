package com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx;

import com.codespair.kafka.navigator.kafkanavigatorbe.model.Topic;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Needs a kafka server running with JMX port enabled on kafka:9992
 */
@Slf4j
public class KafkaJMXIntegrationTest {

  private final static String KAFKA_JMX = "kafka:9992";
  KafkaJMX kafkaJMX = new KafkaJMX();

  @Before
  public void shouldConnectTest() {
    assertTrue(kafkaJMX.connect(KAFKA_JMX));
  }

  @Test
  public void shouldReturnBrokerId() {
    assertThat(kafkaJMX.getBrokerId().get(), is(0));
  }

  @Test
  public void shouldReturAllTopics() {
    Map<String, Topic> topicList = kafkaJMX.getAllTopics();
    log.info("TopicList: {}", topicList);
  }

}
