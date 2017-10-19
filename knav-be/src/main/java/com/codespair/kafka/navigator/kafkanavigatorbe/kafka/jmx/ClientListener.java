package com.codespair.kafka.navigator.kafkanavigatorbe.kafka.jmx;

import lombok.extern.slf4j.Slf4j;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationListener;

@Slf4j
public class ClientListener implements NotificationListener {

  @Override
  public void handleNotification(Notification notification, Object handback) {
    log.info("\nReceived notification:");
    log.info("\tClassName: " + notification.getClass().getName());
    log.info("\tSource: " + notification.getSource());
    log.info("\tType: " + notification.getType());
    log.info("\tMessage: " + notification.getMessage());
    if (notification instanceof AttributeChangeNotification) {
      AttributeChangeNotification acn =
          (AttributeChangeNotification) notification;
      log.info("\tAttributeName: " + acn.getAttributeName());
      log.info("\tAttributeType: " + acn.getAttributeType());
      log.info("\tNewValue: " + acn.getNewValue());
      log.info("\tOldValue: " + acn.getOldValue());
    }
  }
}
