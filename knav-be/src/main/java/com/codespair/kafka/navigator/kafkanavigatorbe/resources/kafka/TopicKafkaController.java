package com.codespair.kafka.navigator.kafkanavigatorbe.resources.kafka;

import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.TopicMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/kafka/topics")
public class TopicKafkaController {

  private final static List<String> hosts = Arrays.asList("kafka:9092", "kafka_1:9093");
  private TopicMeta topicMeta;

  public TopicKafkaController(TopicMeta topicMeta) {
    this.topicMeta = topicMeta;
  }

  @GetMapping
  public ResponseEntity< Map<String, List<String>> > getTopics()  {
    log.info("getting data for kafka hosts: {}", hosts);
    Map<String, List<String>> topics = topicMeta.topicData();
    return ResponseEntity.ok().body(topics);
  }

  @GetMapping("/{topicName}")
  public ResponseEntity<String> getData(@PathVariable String topicName,
                                                     @RequestParam(value="initialOffSet", required=false) String initialOffset,
                                                     @RequestParam(value="numberOfRows", required=false) String numberOfRows) {
    log.info("getting data for topic: {}", topicName);
    return ResponseEntity.ok().body("yey... it works..." + topicName);
  }
}
