package com.codespair.kafka.navigator.kafkanavigatorbe.resources.jmx;

import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.BusClientMetaData;
import com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx.KafkaJMX;
import com.codespair.kafka.navigator.kafkanavigatorbe.model.Broker;
import com.codespair.kafka.navigator.kafkanavigatorbe.model.KMetric;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/jmx/bus")
public class BusJMXController {

  private KafkaJMX kafkaJMX;
  private BusClientMetaData busClientMetaData;

  public BusJMXController(KafkaJMX kafkaJMX, BusClientMetaData busClientMetaData) {
    this.kafkaJMX = kafkaJMX;
    this.busClientMetaData = busClientMetaData;
  }

  @CrossOrigin(origins="*")
  @PostMapping("/connect")
  public ResponseEntity connect(@RequestBody String jmxServerUrl) {
    log.info("Received request to connect, trying to connect with kafka jmx: {}", jmxServerUrl);
    Optional<Boolean> connected = Optional.of(kafkaJMX.connect(jmxServerUrl));
    if(connected.get()) {
      Optional<Broker> broker = kafkaJMX.getBrokerInfo();
      return ResponseEntity.ok().body(broker.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @CrossOrigin(origins="*")
  @PostMapping("/k-client-metrics")
  public ResponseEntity kClientMetrics(@RequestBody List<String> hosts) {
    log.info("Received request to connect, trying to connect and get metrics list of kafka client: {}", hosts);
    Set<KMetric> metricSet = busClientMetaData.initializeClient(hosts);
    return ResponseEntity.ok().body(metricSet);
  }
}
