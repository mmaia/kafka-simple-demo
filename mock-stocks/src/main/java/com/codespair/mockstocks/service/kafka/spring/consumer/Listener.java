package com.codespair.mockstocks.service.kafka.spring.consumer;

//@Component
//@Slf4j
//public class Listener {
//
//    private CountDownLatch countDownLatch = new CountDownLatch(1);
//
//    private static long MESSAGE_COUNTER = 0;
//
//    @KafkaListener(id = "amex-count-by-symbol-consumer-id", topics = "amex-count-by-symbol", group = "amex-count-by-symbol-group")
//    public void listen(ConsumerRecord<String, Long> record, @Header(KafkaHeaders.OFFSET) long offSet) {
//        countDownLatch.countDown();
//
//        if(MESSAGE_COUNTER % 1000 == 0) {
//            log.debug("offset: {}, record: {}", offSet, record);
//        }
//
//        MESSAGE_COUNTER++;
//    }
//}
