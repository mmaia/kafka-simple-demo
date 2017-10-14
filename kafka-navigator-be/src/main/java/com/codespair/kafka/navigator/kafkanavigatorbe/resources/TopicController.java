package com.codespair.kafka.navigator.kafkanavigatorbe.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/topics")
public class TopicController {

  @GetMapping("/{topicName}")
  public ResponseEntity<Map<String, Object>> getData(@PathVariable String topicName,
                                                     @RequestParam(value="initialOffSet", required=false) String initialOffset,
                                                     @RequestParam(value="numberOfRows", required=false) String numberOfRows) {
    log.info("getting data for topic: {}", topicName);
    Map<String, Object> myMap = new HashMap<>();
    myMap.put("working", "Yey.... I am working software");
    return ResponseEntity.ok().body(myMap);
  }
}
