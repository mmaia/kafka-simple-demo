package com.codespair.kafka.navigator.kafkanavigatorbe.resources;

import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.TopicMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/topics")
public class TopicController {

  private final static List<String> hosts = Arrays.asList("kafka:9092", "kafka_1:9093");
  private TopicMeta topicMeta;

  public TopicController(TopicMeta topicMeta) {
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
