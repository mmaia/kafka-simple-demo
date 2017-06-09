package com.codespair.mockstocks.service.kafka.spring.producer;

import com.codespair.mockstocks.model.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class SpringKafkaProducer {

    @Autowired
    private KafkaTemplate<String, StockQuote> kafkaTemplate;

    public void send(String topic, StockQuote message) {
        ListenableFuture<SendResult<String, StockQuote>> future = kafkaTemplate.send(topic, message.getSymbol(), message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, StockQuote>>() {
            @Override
            public void onSuccess(SendResult<String, StockQuote> result) {
                log.debug("sent message='{}', with offset={}, to topic: {}", message,
                        result.getRecordMetadata().offset(), topic);
            }
            @Override
            public void onFailure(Throwable ex) {
                log.error("unable to send message='{}'", message, ex);
            }
        });
    }
}
