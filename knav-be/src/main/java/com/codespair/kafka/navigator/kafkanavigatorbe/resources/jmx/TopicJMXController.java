package com.codespair.kafka.navigator.kafkanavigatorbe.resources.jmx;

import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx.KafkaJMX;
import com.codespair.kafka.navigator.kafkanavigatorbe.model.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/api/jmx/topics")
public class TopicJMXController {

  private KafkaJMX kafkaJMX;

  public TopicJMXController(KafkaJMX kafkaJMX) {
    this.kafkaJMX = kafkaJMX;
  }

  @GetMapping
  public ResponseEntity<Collection<Topic>> allTopics() {
    Collection<Topic> result = kafkaJMX.getAllTopics().values();
    return ResponseEntity.ok(result);
  }

}
