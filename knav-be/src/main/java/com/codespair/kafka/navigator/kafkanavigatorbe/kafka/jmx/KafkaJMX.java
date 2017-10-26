package com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx;

import com.codespair.kafka.navigator.kafkanavigatorbe.model.Broker;
import com.codespair.kafka.navigator.kafkanavigatorbe.model.TopicMetrics;
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

import static com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx.KafkaJMXObjectNamesAndProps.*;

@Slf4j
@Service
@Scope("prototype")
public class KafkaJMX {

  @Getter
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

  /**
   * Build a Broker object with information about it's id, a list of available JMX domains and global topic metrics.
   * @return Broker information.
   */
  public Optional<Broker> getBrokerInfo() {
    Optional<Integer> brokerId = getBrokerId();
    Broker result = Broker.builder()
        .id(brokerId.orElse(-1))
        .allDomains(getJmxDomains().orElse(null))
        .topicMetrics(getTopicMetrics().orElse(null))
        .build();
    return Optional.of(result);
  }


  /**
   * Query all jmx elements and loop in all returned ones printing their name / values to console.
   */
  public void queryAll() {
    try {
      Set mbeans = mbsc.queryNames(new ObjectName(KAFKA_SERVER), null);
      for (Object mbean : mbeans) {
        writeAttributes(mbsc, (ObjectName) mbean);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Recover a list of jmx domains of this kafka server
   *
   * @return a List with strings each representing a jmx domain from the kafka broker
   */
  private Optional<List<String>> getJmxDomains() {
    String domains[];
    try {
      domains = mbsc.getDomains();
      List<String> jmxDomains = Arrays.asList(domains);
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
  private Optional<Integer> getBrokerId() {
    Optional<Integer> brokerId = Optional.empty();
    try {
      ObjectName objectName = new ObjectName(KAFKA_SERVER_APP_INFO);
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

  /**
   * Get general topic metrics for broker
   *
   * @return AttributeList with topic metrics from broker.
   */
  private Optional<TopicMetrics> getTopicMetrics() {
    Optional<TopicMetrics> result = Optional.empty();
    try {
      ObjectName objectName = new ObjectName(KAFKA_SERVER_BROKER_TOPIC_METRICS);
      AttributeList attributeList = mbsc.getAttributes(objectName, topicMetricsAttributes());
      TopicMetrics topicMetrics = buildTopicMetrics(attributeList);
      result = Optional.of(topicMetrics);
    } catch (Exception e) {
      log.error("Error recovering Topic Metrics for Broker: {}",
          getBrokerId().get(), e.getMessage(), e);

    }
    return result;
  }

  private TopicMetrics buildTopicMetrics(AttributeList attributeList) {
    TopicMetrics topicMetrics = null;
    for (Object obj: attributeList) {
      Attribute attribute = (Attribute) obj;
      log.info("attribute: {}", attribute);
    }
    return topicMetrics;
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


  private MBeanServerConnection mBeanServerConnection(String url) throws IOException {

    log.info("connecting to mbeanserver url: {}", url);
    JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(url), defaultJMXConnectorProperties());
    return jmxc.getMBeanServerConnection();
  }

  private void logDomains(String[] domains) {
    Arrays.sort(domains);
    for (String domain : domains) {
      log.info("\tDomain = " + domain);
    }
  }
}
