package com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@Scope("prototype")
@Getter
public class KafkaJMX {

  private JMXServiceURL jmxServiceURL;
  private List<String> jmxDomains;
  private boolean connected;
  private MBeanServerConnection mbsc;

  /**
   * Connects with kafka jmx and return true if successful, false otherwise.
   *
   * @param jmxUrl the url of a broker to connect with using JMX.
   * @return a list of available JMX Bean domain names to be navigated.
   */
  public boolean connect(String jmxUrl) {
    String url = "service:jmx:rmi:///jndi/rmi://" + jmxUrl + "/jmxrmi";
    try {
      mbsc = mBeanServerConnection(url);
      connected = true;
      return isConnected();
    } catch (IOException e) {
      log.error("could not connect to jmx kafka server: {}", e.getMessage(), e);
    }
  }

  public Optional<List<String>> getJmxDomains() {
    String domains[];
    try {
      domains = mbsc.getDomains();
      jmxDomains = Arrays.asList(domains);
      logDomains(domains);
      return Optional.of(jmxDomains);
    } catch (IOException e) {
      log.error("Error recovering JMX Domains: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }

  /**
   * Recover the Broker id
   *
   * @return an Integer representing the Broker id
   */
  public Optional<Integer> getBrokerId() {
    try {
      ObjectName objectName = new ObjectName("kafka.server:type=app-info");
      return Optional.of((Integer) (mbsc.getAttribute(objectName, "id")));
    } catch (Exception e) {
      log.error("Error getting Broker id: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }

  public Optional<AttributeList> getTopicMetrics() {
    try {
      ObjectName objectName = new ObjectName("kafka.server:type=BrokerTopicMetrics");
      Optional.of(mbsc.getAttributes(objectName, topicMetricsAttributes()));

    } catch (Exception e) {
      log.error("Erorr recovering Topic Metrics for Broker: {}",
          getBrokerId().get());
      return Optional.empty();
    }
  }

  private String[] topicMetricsAttributes() {
    final String[] attributes = {
        "BytesInPerSec",
        "BytesOutPerSec",
        "BytesRejectPerSec",
        "FailedFetchRequestsPerSec",
        "FailedProduceRequestsPerSec",
        "MessagesInPerSec",
        "TotalFetchRequestsPerSec",
        "TotalProduceRequestsPerSec"
    };
    return attributes;
  }


  private MBeanServerConnection mBeanServerConnection(String url) throws IOException {
    log.info("connecting to mbeanserver url: {}", url);
    jmxServiceURL = new JMXServiceURL(url);
    JMXConnector jmxc = JMXConnectorFactory.connect(jmxServiceURL, defaultJMXConnectorProperties());
    return jmxc.getMBeanServerConnection();
  }

  private void logDomains(String[] domains) {
    Arrays.sort(domains);
    for (String domain : domains) {
      log.info("\tDomain = " + domain);
    }
  }

  private Map<String, String> defaultJMXConnectorProperties() {
    Map<String, String> props = new HashMap<>();
    props.put("jmx.remote.x.request.waiting.timeout", "3000");
    props.put("jmx.remote.x.notification.fetch.timeout", "3000");
    props.put("sun.rmi.transport.connectionTimeout", "3000");
    props.put("sun.rmi.transport.tcp.handshakeTimeout", "3000");
    props.put("sun.rmi.transport.tcp.responseTimeout", "3000");
    return props;
  }
}
