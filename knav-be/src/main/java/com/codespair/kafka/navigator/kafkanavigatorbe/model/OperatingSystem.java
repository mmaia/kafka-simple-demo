package com.codespair.kafka.navigator.kafkanavigatorbe.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Data
public class OperatingSystem {

  private Map<String, Object> attributes=new HashMap<>();

  public void addAttribute(String key,Object value){
    attributes.put(key,value);
  }
  public static List<String> osAttributeNames() {
    return asList("Name", "Arch", "AvailableProcessors", "ProcessCpuLoad", "SystemCpuLoad");
  }
}
