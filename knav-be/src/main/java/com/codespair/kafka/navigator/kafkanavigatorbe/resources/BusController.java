package com.codespair.kafka.navigator.kafkanavigatorbe.resources;

import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.BusMeta;
import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.KMetric;
import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.TopicMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/bus")
public class BusController {

  private BusMeta busMeta;

  public BusController(BusMeta busMeta) {
    this.busMeta = busMeta;
  }

  @CrossOrigin(origins="*")
  @PostMapping("/connect")
  public ResponseEntity connect(@RequestBody List<String> hosts) {
    log.info("Received request to connect, trying to connect with kafka servers: {}", hosts);
    Set<KMetric> metricSet = busMeta.initializeClient(hosts);
    return ResponseEntity.ok().body(metricSet);
  }
}
