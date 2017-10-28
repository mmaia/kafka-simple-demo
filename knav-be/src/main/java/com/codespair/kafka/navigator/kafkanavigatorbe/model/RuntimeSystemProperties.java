package com.codespair.kafka.navigator.kafkanavigatorbe.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class RuntimeSystemProperties {
  private Map<String, Object> attributes=new HashMap<>();

  public void addAttribute(String key,Object value){
    attributes.put(key,value);
  }
  public static List<String> osAttributeNames() {
    return asList(
        "com.sun.management.jmxremote.port",
        "com.sun.management.jmxremote.rmi.port",
        "java.runtime.version",
        "java.rmi.server.hostname",
        "ProcessCpuLoad",
        "SystemCpuLoad");
  }
}
