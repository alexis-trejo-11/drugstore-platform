package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.messaging.kafka.producer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.notification.EmailVerificationEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.notification.TwoFactorCodeEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.notification.WelcomeEmailEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.NotificationEventPublisher;

public class NotificationEventProducer implements NotificationEventPublisher {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationEventProducer.class);
  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  public NotificationEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Value("${kafka.topics.notification.email-verification}")
  private String emailVerificationTopic;

  @Value("${kafka.topics.notification.two-factor-code}")
  private String twoFactorCodeTopic;

  @Value("${kafka.topics.notification.welcome-email}")
  private String welcomeEmailTopic;

  @Value("${kafka.producer.timeout-seconds:10}")
  private long timeoutSeconds;

  @Override
  public boolean publishEmailVerification(EmailVerificationEvent event) {
    log.info("Publishing EmailVerificationEvent for userId: {}, email: {}",
        event.userId(), event.email());

    try {
      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          emailVerificationTopic,
          event.userId(), // Key por userId
          event);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("EmailVerificationEvent published. EventId: {}, Offset: {}",
              event.eventId(), result.getRecordMetadata().offset());
        } else {
          log.error("Failed to publish EmailVerificationEvent. EventId: {}",
              event.eventId(), ex);
        }
      });

      future.get(timeoutSeconds, TimeUnit.SECONDS);
      return true;

    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error publishing EmailVerificationEvent for userId: {}",
          event.userId(), e);
      return false;
    }
  }

  @Override
  public boolean publishTwoFactorCode(TwoFactorCodeEvent event) {
    log.info("Publishing TwoFactorCodeEvent for userId: {}, purpose: {}",
        event.userId(), event.purpose());

    try {
      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          twoFactorCodeTopic,
          event.userId(),
          event);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("TwoFactorCodeEvent published. EventId: {}, Offset: {}",
              event.eventId(), result.getRecordMetadata().offset());
        } else {
          log.error("Failed to publish TwoFactorCodeEvent. EventId: {}",
              event.eventId(), ex);
        }
      });

      future.get(timeoutSeconds, TimeUnit.SECONDS);
      return true;

    } catch (Exception e) {
      log.error("Error publishing TwoFactorCodeEvent for userId: {}",
          event.userId(), e);
      return false;
    }
  }

  @Override
  public boolean publishWelcomeEmail(WelcomeEmailEvent event) {
    log.info("Publishing WelcomeEmailEvent for userId: {}, email: {}",
        event.userId(), event.email());

    try {
      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          welcomeEmailTopic,
          event.userId(),
          event);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("WelcomeEmailEvent published. EventId: {}, Offset: {}",
              event.eventId(), result.getRecordMetadata().offset());
        } else {
          log.error("Failed to publish WelcomeEmailEvent. EventId: {}",
              event.eventId(), ex);
        }
      });

      future.get(timeoutSeconds, TimeUnit.SECONDS);
      return true;

    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error publishing WelcomeEmailEvent for userId: {}",
          event.userId(), e);
      return false;
    }
  }
}
