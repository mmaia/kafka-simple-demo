package com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx;

import com.codespair.kafka.navigator.kafkanavigatorbe.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.*;

import static com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx.KafkaJMXObjectNamesAndProps.*;

@Slf4j
@Service
public class KafkaJMX {

  @Getter
  private boolean connected;
  private MBeanServerConnection mbsc;

  /**
   * Connects with kafka jmx and return true if successful, false otherwise.
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
        .jmxDomains(getJmxDomains().orElse(null))
        .topicMetricList(getBrokerTopicMetrics().orElse(null))
        .os(getOsInfo().orElse(null))
        .kafkaVersion(getKafkaVersion().orElse(null))
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
   * @return a List with strings each representing a jmx domain from the kafka broker
   */
  private Optional<List<String>> getJmxDomains() {
    String domains[];
    try {
      domains = mbsc.getDomains();
      List<String> jmxDomains = Arrays.asList(domains);
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
   * @return an Integer representing the Broker id
   */
  Optional<Integer> getBrokerId() {
    Optional<Integer> brokerId = Optional.empty();
    try {
      String searchString = KAFKA_SERVER_APP_INFO + ",*";
      ObjectName objectName = new ObjectName(searchString);
      Set mbeans = mbsc.queryNames(objectName, null);
      for (Object mbean : mbeans) {
        ObjectName oName = (ObjectName) mbean;
        String sName = oName.toString();
        if (sName.contains("app-info")) {
          int idPos = sName.indexOf("id=");
          brokerId = Optional.of(Integer.parseInt(sName.substring(idPos + 3).trim()));
        }
      }
    } catch (Exception e) {
      log.error("Error getting Broker id: {}", e.getMessage(), e);
    }
    return brokerId;
  }

  public Map<String, Topic> getAllTopics() {
    Map<String, Topic> topicMap = null;
    String searchString = KAFKA_SERVER_ALL_TOPICS_METRICS + ",*";
    ObjectName objectName = null;
    try {
      objectName = new ObjectName(searchString);
      Set mbeans = mbsc.queryNames(objectName, null);
      topicMap = processTopicData(mbeans);
    } catch (Exception e) {
      log.info("error recovering topics: {}", e.getMessage(), e);
    }
    return topicMap;
  }

  /**
   * Initial and naive implementation, it does the trick by now.
   * Definitely some work to do here in the near future
   * @param mbeans list of mbeans
   * @return a set with unique topic names
   */
  private Map<String, Topic> processTopicData(Set mbeans) throws Exception {
    Map<String, Topic> result = new HashMap<>();
    for (Object mbean : mbeans) {
      ObjectName oName = (ObjectName) mbean;
      String sName = oName.toString();
      if(sName.contains("topic")) {
        String topicName = topicName(sName);
        String metricName = metricName(sName);
        TopicMetric topicMetric = buildTopicMetric(oName, metricName);
        Topic topic = buildTopic(result, topicName, topicMetric);
        result.put(topicName, topic);
      }
    }
    return result;
  }

  private Topic buildTopic(Map<String, Topic> result, String topicName, TopicMetric topicMetric) {
    Topic topic;
    if(result.get(topicName) != null) {
      topic = result.get(topicName);
      topic.addMetric(topicMetric);
    } else {
      topic = new Topic();
      topic.setName(topicName);
      topic.addMetric(topicMetric);
    }
    return topic;
  }

  private String metricName(String sName) {
    int namePos = sName.lastIndexOf("name=");
    return sName.substring(namePos + 5, sName.lastIndexOf(","));
  }

  private String topicName(String sName) {
    int topicPos = sName.lastIndexOf("topic=");
    return sName.substring(topicPos + 6);
  }

  public Optional<String> getKafkaVersion() {
    Optional<String> kafkaVersion = Optional.empty();
    Integer brokerId = getBrokerId().orElse(-1);
    String searchString = KAFKA_SERVER_APP_INFO + ",id=" + brokerId;
    try {
      ObjectName objectName = new ObjectName(searchString);
      String version = (String)mbsc.getAttribute(objectName, "Version");
      kafkaVersion = Optional.of(version);
    } catch (Exception e) {
      log.error("Could not recover kafka version: {}", e.getMessage(), e);
    }

    return kafkaVersion;
  }

  public Optional<OperatingSystem> getOsInfo() {
    Optional<OperatingSystem> result = Optional.empty();
    try {
      ObjectName objectName = new ObjectName(HOST_OS);
      OperatingSystem os = buildOperatingSystem(objectName);
      result = Optional.of(os);
    } catch (Exception e) {
      log.error("Could not recover host name: {}", e.getMessage(), e);
    }
    return result;
  }

  /**
   * Get general topic metrics for broker
   * @return AttributeList with topic metrics from broker.
   */
  private Optional<List<TopicMetric>> getBrokerTopicMetrics() {
    Optional<List<TopicMetric>> result = Optional.empty();
    List<TopicMetric> topicMetricList = new ArrayList<>();
    try {
      for (TopicMetricAttributeType tmat : TopicMetricAttributeType.values()) {
        final String sName = KAFKA_SERVER_BROKER_TOPIC_METRICS + tmat.toString();
        ObjectName objectName = new ObjectName(sName);
        TopicMetric topicMetric = buildTopicMetric(objectName, tmat.toString());
        topicMetricList.add(topicMetric);
      }
    } catch (Exception e) {
      log.error("Error recovering Topic Metrics for Broker: {}",
          getBrokerId().get(), e.getMessage(), e);
    }
    return Optional.of(topicMetricList);
  }

  private OperatingSystem buildOperatingSystem(ObjectName objectName) throws Exception {
    OperatingSystem operatingSystem = new OperatingSystem();
    for (String attribute: OperatingSystem.osAttributeNames()) {
      operatingSystem.addAttribute(attribute, mbsc.getAttribute(objectName, attribute));
    }
    return operatingSystem;
  }

  /**
   * @param objectName The mbean object name from where all topic metrics will be collected and returned
   * in a TopicMetric object.
   * @return TopMetric with a map containing all attributes for a specific metric
   * @throws Exception
   */
  private TopicMetric buildTopicMetric(ObjectName objectName, String metric) throws Exception {
    TopicMetric topicMetric = new TopicMetric();
    for (String attribute : TopicMetric.topicMetricAttributeNames()) {
      topicMetric.addAttribute(attribute, mbsc.getAttribute(objectName, attribute));
      topicMetric.setTopicMetricAttributeType(TopicMetricAttributeType.fromString(metric));
    }
    return topicMetric;
  }

  /**
   * Starts a JMX Connection with the specified kafka server
   * @param url $jmxUrl:$jmxPort
   * @return a jmx connection that can be used to query the mbeans
   * @throws IOException in case the connection can't be established
   */
  private MBeanServerConnection mBeanServerConnection(String url) throws IOException {
    log.info("connecting to mbeanserver url: {}", url);
    JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(url), defaultJMXConnectorProperties());
    return jmxc.getMBeanServerConnection();
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

}
