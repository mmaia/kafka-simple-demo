package com.codespair.kafka.navigator.kafkanavigatorbe.resources;

import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.TopicMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/topics")
public class TopicController {

  private final static List<String> hosts = Arrays.asList("kafka:9092", "kafka_1:9093");
  private final static String topic = "stock-quote";

  TopicMeta topicMeta;

  public TopicController() {
    topicMeta = new TopicMeta(hosts);
  }

  @GetMapping("/")
  public ResponseEntity<Map<String, Object>> getTopics()  {
    log.info("getting data for kafka hosts: {}", hosts);
    Map<String, Object> myMap = new HashMap<>();
    myMap.put("working", "Yey.... I am working software");
    topicMeta.topicData();
    return ResponseEntity.ok().body(myMap);
  }

  @GetMapping("/{topicName}")
  public ResponseEntity<String> getData(@PathVariable String topicName,
                                                     @RequestParam(value="initialOffSet", required=false) String initialOffset,
                                                     @RequestParam(value="numberOfRows", required=false) String numberOfRows) {
    log.info("getting data for topic: {}", topicName);
    return ResponseEntity.ok().body("yey... it works..." + topicName);
  }
}
