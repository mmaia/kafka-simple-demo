package com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class KafkaJMX {

  JMXServiceURL jmxServiceURL;

  /**
   * Connects with kafka jmx
   * @param jmxUrl
   */
  public void connect(String jmxUrl) {
    String url = "service:jmx:rmi:///jndi/rmi://" + jmxUrl + "/jmxrmi";
    try {
      MBeanServerConnection mbsc = mBeanServerConnection(url);
      String domains[] = mbsc.getDomains();
      logDomains(domains);
      log.info("\n---------------------\n");
      log.info("defaultDomain: " + mbsc.getDefaultDomain());
    } catch (IOException e) {
      log.error("could not connect to jmx kafka server: {}", e.getMessage(), e);
    }
  }

  private MBeanServerConnection mBeanServerConnection(String url) throws IOException {
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
