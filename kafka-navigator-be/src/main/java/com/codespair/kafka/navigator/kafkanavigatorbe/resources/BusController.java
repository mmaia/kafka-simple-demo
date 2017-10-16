package com.codespair.kafka.navigator.kafkanavigatorbe.resources;

import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.BusMeta;
import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.TopicMeta;
import com.codespair.kafka.navigator.kafkanavigatorbe.resources.utils.HostsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/bus")
public class BusController {

  private BusMeta busMeta;
  private TopicMeta topicMeta;

  public BusController(BusMeta busMeta, TopicMeta topicMeta) {
    this.busMeta = busMeta;
    this.topicMeta = topicMeta;
  }

  @PostMapping("/connect")
  public ResponseEntity connect(@RequestBody HostsWrapper hw) {
    busMeta.initializeClient(hw.getHosts());
    return ResponseEntity.ok("Connection established with hosts: " + hw.getHosts());
  }
}
