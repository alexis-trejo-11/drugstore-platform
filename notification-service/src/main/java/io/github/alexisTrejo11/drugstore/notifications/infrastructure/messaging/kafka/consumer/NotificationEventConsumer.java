package io.github.alexisTrejo11.drugstore.notifications.infrastructure.messaging.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.notifications.domain.event.EmailVerificationEvent;
import io.github.alexisTrejo11.drugstore.notifications.domain.event.TwoFactorCodeEvent;
import io.github.alexisTrejo11.drugstore.notifications.domain.event.WelcomeEmailEvent;
import io.github.alexisTrejo11.drugstore.notifications.infrastructure.messaging.kafka.handler.EmailVerificationEventHandler;
import io.github.alexisTrejo11.drugstore.notifications.infrastructure.messaging.kafka.handler.TwoFactorCodeEventHandler;
import io.github.alexisTrejo11.drugstore.notifications.infrastructure.messaging.kafka.handler.WelcomeEmailEventHandler;

/**
 * Kafka consumer for notification events
 * 
 * Listens to notification topics and delegates processing
 * to appropriate handlers
 */
@Component
public class NotificationEventConsumer {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationEventConsumer.class);
  private final EmailVerificationEventHandler emailVerificationHandler;
  private final TwoFactorCodeEventHandler twoFactorCodeHandler;
  private final WelcomeEmailEventHandler welcomeEmailHandler;
  private final ObjectMapper objectMapper;

  @Autowired
  public NotificationEventConsumer(
      EmailVerificationEventHandler emailVerificationHandler,
      TwoFactorCodeEventHandler twoFactorCodeHandler,
      WelcomeEmailEventHandler welcomeEmailHandler,
      ObjectMapper objectMapper) {
    this.emailVerificationHandler = emailVerificationHandler;
    this.twoFactorCodeHandler = twoFactorCodeHandler;
    this.welcomeEmailHandler = welcomeEmailHandler;
    this.objectMapper = objectMapper;
  }

  /**
   * Consume email verification events
   */
  @KafkaListener(topics = "${kafka.topics.notification.email-verification}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
  public void consumeEmailVerification(
      @Payload String payload,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      Acknowledgment acknowledgment) {

    log.info("Received EmailVerificationEvent - Topic: {}, Partition: {}, Offset: {}",
        topic, partition, offset);

    try {
      // Deserialize event
      EmailVerificationEvent event = objectMapper.readValue(payload, EmailVerificationEvent.class);

      log.debug("Deserialized EmailVerificationEvent - EventId: {}, UserId: {}",
          event.eventId(), event.userId());

      // Process event
      emailVerificationHandler.handle(event);

      // Acknowledge successful processing
      acknowledgment.acknowledge();

      log.info("EmailVerificationEvent processed and acknowledged - EventId: {}, Offset: {}",
          event.eventId(), offset);

    } catch (Exception e) {
      log.error("Error consuming EmailVerificationEvent - Topic: {}, Partition: {}, Offset: {}",
          topic, partition, offset, e);

      // Don't acknowledge - message will be reprocessed
      // In production, consider implementing DLQ after N retries
      throw new RuntimeException("Failed to process EmailVerificationEvent", e);
    }
  }

  /**
   * Consume two-factor code events
   */
  @KafkaListener(topics = "${kafka.topics.notification.two-factor-code}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
  public void consumeTwoFactorCode(
      @Payload String payload,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      Acknowledgment acknowledgment) {

    log.info("Received TwoFactorCodeEvent - Topic: {}, Partition: {}, Offset: {}",
        topic, partition, offset);

    try {
      TwoFactorCodeEvent event = objectMapper.readValue(payload, TwoFactorCodeEvent.class);

      log.debug("Deserialized TwoFactorCodeEvent - EventId: {}, UserId: {}, Channel: {}",
          event.eventId(), event.userId(), event.channel());

      twoFactorCodeHandler.handle(event);

      acknowledgment.acknowledge();

      log.info("TwoFactorCodeEvent processed and acknowledged - EventId: {}, Offset: {}",
          event.eventId(), offset);

    } catch (Exception e) {
      log.error("Error consuming TwoFactorCodeEvent - Topic: {}, Partition: {}, Offset: {}",
          topic, partition, offset, e);
      throw new RuntimeException("Failed to process TwoFactorCodeEvent", e);
    }
  }

  /**
   * Consume welcome email events
   */
  @KafkaListener(topics = "${kafka.topics.notification.welcome-email}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
  public void consumeWelcomeEmail(
      @Payload String payload,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      Acknowledgment acknowledgment) {

    log.info("Received WelcomeEmailEvent - Topic: {}, Partition: {}, Offset: {}",
        topic, partition, offset);

    try {
      WelcomeEmailEvent event = objectMapper.readValue(payload, WelcomeEmailEvent.class);

      log.debug("Deserialized WelcomeEmailEvent - EventId: {}, UserId: {}",
          event.eventId(), event.userId());

      welcomeEmailHandler.handle(event);

      acknowledgment.acknowledge();

      log.info("WelcomeEmailEvent processed and acknowledged - EventId: {}, Offset: {}",
          event.eventId(), offset);

    } catch (Exception e) {
      log.error("Error consuming WelcomeEmailEvent - Topic: {}, Partition: {}, Offset: {}",
          topic, partition, offset, e);
      throw new RuntimeException("Failed to process WelcomeEmailEvent", e);
    }
  }
}