package com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx;

import com.codespair.kafka.navigator.kafkanavigatorbe.model.Broker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.management.*;
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
    } catch (IOException e) {
      log.error("could not connect to jmx kafka server: {}", e.getMessage(), e);
    }
    return isConnected();
  }

  public Optional<Broker> getBrokerInfo() {
    Optional<Integer> brokerId = getBrokerId();
    Broker result = Broker.builder()
        .id(brokerId.get())
        .build();
    return Optional.of(result);
  }

  /**
   * Recover a list of jmx domains of this kafka server
   *
   * @return a List with strings each representing a jmx domain from the kafka broker
   */
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
   * Recover the Broker id from the ObjectName. This is an initial implementation that might not be the best way of
   * doing this but it's the current known way of getting this done.
   * This method parses the ObjectName and look for the one containing app-info and then substring from the "id="
   * pattern position until the end, parsing it to an Integer that represents the kafka broker id.
   *
   * @return an Integer representing the Broker id
   */
  public Optional<Integer> getBrokerId() {
    Optional<Integer> brokerId = Optional.empty();
    try {
      ObjectName objectName = new ObjectName("kafka.server:*");
      Set mbeans = mbsc.queryNames(objectName, null);
      for (Object mbean : mbeans) {
        ObjectName oName = (ObjectName) mbean;
        String sName = oName.toString();
        if(sName.contains("app-info")) {
          int idPos = sName.indexOf("id=");
          brokerId = Optional.of(Integer.parseInt(sName.substring(idPos + 3).trim()));
        }
      }
    } catch (Exception e) {
      log.error("Error getting Broker id: {}", e.getMessage(), e);
    }
    return brokerId;
  }

  public void queryAll() {
    try {
      Set mbeans = mbsc.queryNames(new ObjectName("kafka.server:*"), null);
      for (Object mbean : mbeans) {
        writeAttributes(mbsc, (ObjectName) mbean);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void writeAttributes(MBeanServerConnection mBeanServerConnection, ObjectName objectName) {
    try {
      MBeanInfo mBeanInfo = mbsc.getMBeanInfo(objectName);
      log.info("\n====================================\n");
      log.info("Object Name: {}", objectName);
      log.info("\n====================================\n");
      MBeanAttributeInfo[] attributeInfos = mBeanInfo.getAttributes();
      for (MBeanAttributeInfo attributeInfo : attributeInfos) {
        log.info("\nAttribute Name:\t {}, \nAttribute Desc:\t {}, \nAttribute Type:\t {}",
            attributeInfo.getName(), attributeInfo.getDescription(), attributeInfo.getType());
      }
    } catch (Exception e) {
      log.error("Error processing attributes from mebean, objectName: {}, error message: {}", objectName, e.getMessage());
    }
  }

  /**
   * Get general topic metrics for broker
   *
   * @return AttributeList with topic metrics from broker.
   */
  public Optional<AttributeList> getTopicMetrics() {
    try {
      ObjectName objectName = new ObjectName("kafka.server:type=BrokerTopicMetrics");
      return Optional.of(mbsc.getAttributes(objectName, topicMetricsAttributes()));
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
