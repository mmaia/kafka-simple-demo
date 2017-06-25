package com.codespair.mockstocks.service.kafka.spring.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class Listener {

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private static long MESSAGE_COUNTER = 0;

    @KafkaListener(id = "amex-count-by-symbol-consumer-id", topics = "amex-count-by-symbol", group = "amex-count-by-symbol-group")
    public void listen(ConsumerRecord<String, Long> record, @Header(KafkaHeaders.OFFSET) long offSet) {
        countDownLatch.countDown();

        if(MESSAGE_COUNTER % 1000 == 0) {
            log.debug("offset: {}, record: {}", offSet, record);
        }

        MESSAGE_COUNTER++;
    }
}
