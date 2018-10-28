package com.codespair.mockstocks.service.kafka.stream;

import com.codespair.mockstocks.model.StockQuote;
import lombok.Setter;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

@Setter
public class JsonSerde<T> implements Serde<T> {

  private Serializer<T> serializer = new JsonPojoSerializer<>();
  private Deserializer<T> deserializer = new JsonPojoDeserializer<>();

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    Map<String, Object> serdeProps = new HashMap<>();
    serdeProps.put("jsonPOJOClass", StockQuote.class);
    deserializer.configure(serdeProps, isKey);
    serializer.configure(configs, isKey);
  }

  public void configureValue() {
    this.configure(null, false);
  }

  @Override
  public void close() {
    serializer.close();
    deserializer.close();
  }

  @Override
  public Serializer<T> serializer() {
    return serializer;
  }

  @Override
  public Deserializer<T> deserializer() {
    return deserializer;
  }
}
