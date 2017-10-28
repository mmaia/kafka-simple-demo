package com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx;

import java.util.HashMap;
import java.util.Map;

class KafkaJMXObjectNamesAndProps {

  final static String HOST_OS = "java.lang:type=OperatingSystem";
  final static String KAFKA_SERVER = "kafka.server:*";
  final static String KAFKA_SERVER_APP_INFO = "kafka.server:type=app-info";

  final static String KAFKA_SERVER_BROKER_TOPIC_METRICS = "kafka.server:type=BrokerTopicMetrics,name=";

  /**
   * Configuration used for the jmx connection with kafka
   * @return a map with jmx connection values for the kafka connection
   */
  static Map<String, String> defaultJMXConnectorProperties() {
    Map<String, String> props = new HashMap<>();
    props.put("jmx.remote.x.request.waiting.timeout", "300000");
    props.put("jmx.remote.x.notification.fetch.timeout", "300000");
    props.put("sun.rmi.transport.connectionTimeout", "300000");
    props.put("sun.rmi.transport.tcp.handshakeTimeout", "300000");
    props.put("sun.rmi.transport.tcp.responseTimeout", "300000");
    return props;
  }
}
