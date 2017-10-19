package com.codespair.kafka.navigator.kafkanavigatorbe.resources;

import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.BusClientMetaData;
import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.KMetric;
import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx.KafkaJMX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/bus")
public class BusController {

  private KafkaJMX kafkaJMX;
  private BusClientMetaData busClientMetaData;

  public BusController(KafkaJMX kafkaJMX, BusClientMetaData busClientMetaData) {
    this.kafkaJMX = kafkaJMX;
    this.busClientMetaData = busClientMetaData;
  }

  @CrossOrigin(origins="*")
  @PostMapping("/connect/{server_url}")
  public ResponseEntity connect(@PathVariable("server_url") String jmxServerUrl) {
    log.info("Received request to connect, trying to connect with kafka jmx: {}", jmxServerUrl);
    Optional<List<String>> domainList =  kafkaJMX.connect(jmxServerUrl);
    if(domainList.isPresent())
    {
      return ResponseEntity.ok().body(domainList.get());
    }
    return ResponseEntity.noContent().build();
  }


  @CrossOrigin(origins="*")
  @PostMapping("/k-client-metrics")
  public ResponseEntity kClientMetrics(@RequestBody List<String> hosts) {
    log.info("Received request to connect, trying to connect and get metrics list of kafka client: {}", hosts);
    Set<KMetric> metricSet = busClientMetaData.initializeClient(hosts);
    return ResponseEntity.ok().body(metricSet);
  }
}
