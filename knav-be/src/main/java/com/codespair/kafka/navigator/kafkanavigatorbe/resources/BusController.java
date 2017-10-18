package com.codespair.kafka.navigator.kafkanavigatorbe.resources;

import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.BusMeta;
import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.KMetric;
import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.TopicMeta;
import com.codespair.kafka.navigator.kafkanavigatorbe.resources.utils.HostsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

  @CrossOrigin(origins="*")
  @PostMapping("/connect")
  public ResponseEntity connect(@RequestBody HostsWrapper hw) {
    log.info("Received request to connect, trying to connect with kafka servers: {}", hw.getHosts());
    Set<KMetric> metricSet = busMeta.initializeClient(hw.getHosts());
    return ResponseEntity.ok().body(metricSet);
  }
}
