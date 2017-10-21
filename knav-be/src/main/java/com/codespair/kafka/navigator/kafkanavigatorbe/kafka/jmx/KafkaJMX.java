package com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class KafkaJMX {

  private JMXServiceURL jmxServiceURL;
  private List<String> jmxDomains;
  private boolean isConnected;


  /**
   * Connects with kafka jmx and recover a list of available MBean Domain names.
   *
   * @param jmxUrl the url of a broker to connect with using JMX.
   * @return a list of available JMX Bean domain names to be navigated.
   */
  public Optional<List<String>> connect(String jmxUrl) {
    String url = "service:jmx:rmi:///jndi/rmi://" + jmxUrl + "/jmxrmi";
    try {
      MBeanServerConnection mbsc = mBeanServerConnection(url);
      String domains[] = mbsc.getDomains();
      isConnected = true;
      jmxDomains = Arrays.asList(domains);
      logDomains(domains);
      return Optional.of(jmxDomains);
    } catch (IOException e) {
      log.error("could not connect to jmx kafka server: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }

  public List<String> getJmxDomains() {
    return jmxDomains;
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
