package com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx;

import java.util.HashMap;
import java.util.Map;

class KafkaJMXObjectNamesAndProps {

  final static String HOST_OS = "java.lang:type=OperatingSystem";
  final static String KAFKA_SERVER = "kafka.server:*";
  final static String KAFKA_SERVER_APP_INFO = "kafka.server:type=app-info,*";
  final static String KAFKA_SERVER_BROKER_TOPIC_METRICS= "kafka.server:type=BrokerTopicMetrics,*";


  /**
   * Configuration used for the jmx connection with kafka
   * @return a map with jmx connection values for the kafka connection
   */
  static Map<String, String> defaultJMXConnectorProperties() {
    Map<String, String> props = new HashMap<>();
    props.put("jmx.remote.x.request.waiting.timeout", "3000");
    props.put("jmx.remote.x.notification.fetch.timeout", "3000");
    props.put("sun.rmi.transport.connectionTimeout", "3000");
    props.put("sun.rmi.transport.tcp.handshakeTimeout", "3000");
    props.put("sun.rmi.transport.tcp.responseTimeout", "3000");
    return props;
  }

  /**
   * Contains list of global topic attributes from Kafka
   * @return a string array with kafka topic metrics attribute names.
   */
  static String[] topicMetricsAttributes() {
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
}
