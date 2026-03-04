package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.messaging.kafka.producer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.user.UserCreatedEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.user.UserDeletedEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.user.UserUpdatedEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserEventPublisher;

@Component
public class UserEventProducer implements UserEventPublisher {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserEventProducer.class);

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Value("${kafka.topics.user.created}")
  private String userCreatedTopic;

  @Value("${kafka.topics.user.updated}")
  private String userUpdatedTopic;

  @Value("${kafka.topics.user.deleted}")
  private String userDeletedTopic;

  @Value("${kafka.topics.user.registered:user-registered}")
  private String userRegisteredTopic;

  @Value("${kafka.topics.user.login:user-login}")
  private String userLoginTopic;

  @Value("${kafka.topics.auth.password-changed:auth.password-changed}")
  private String passwordChangedTopic;

  @Value("${kafka.topics.auth.account-activated:auth.account-activated}")
  private String accountActivatedTopic;

  @Value("${kafka.topics.auth.two-factor-enabled:auth.two-factor-enabled}")
  private String twoFactorEnabledTopic;

  @Value("${kafka.topics.auth.two-factor-disabled:auth.two-factor-disabled}")
  private String twoFactorDisabledTopic;

  @Value("${kafka.producer.timeout-seconds:10}")
  private long timeoutSeconds;

  @Autowired
  public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void publishUserRegistered(UserRegisteredEvent event) {
    log.info("Publishing UserRegisteredEvent for userId: {}", event.userId());

    try {
      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          userRegisteredTopic,
          event.userId(),
          event);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("UserRegisteredEvent published successfully. Topic: {}, Partition: {}, Offset: {}",
              result.getRecordMetadata().topic(),
              result.getRecordMetadata().partition(),
              result.getRecordMetadata().offset());
        } else {
          log.error("Failed to publish UserRegisteredEvent for userId: {}. Error: {}",
              event.userId(), ex.getMessage(), ex);
        }
      });

      future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error publishing UserRegisteredEvent for userId: {}", event.userId(), e);
      // Non-blocking: registration should succeed even if event publishing fails
    }
  }

  @Override
  public void publishUserLogin(UserLoginEvent event) {
    log.info("Publishing UserLoginEvent for userId: {}", event.userId());

    try {
      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          userLoginTopic,
          event.userId(),
          event);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.debug("UserLoginEvent published successfully");
        } else {
          log.warn("Failed to publish UserLoginEvent for userId: {}. Error: {}",
              event.userId(), ex.getMessage());
        }
      });

      // Don't wait for completion - login events are non-critical
      future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.warn("Error publishing UserLoginEvent for userId: {} - continuing anyway", event.userId());
      // Non-blocking: login should succeed even if event publishing fails
    }
  }

  @Override
  public boolean publishUserCreated(UserCreatedEvent event) {
    log.info("Publishing UserCreatedEvent for userId: {}, eventId: {}",
        event.userId(), event.eventId());

    try {
      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          userCreatedTopic, // Topic
          event.userId(), // Key (para particionamiento)
          event // Value (el evento)
      );

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("UserCreatedEvent published successfully. " +
              "EventId: {}, Topic: {}, Partition: {}, Offset: {}",
              event.eventId(),
              result.getRecordMetadata().topic(),
              result.getRecordMetadata().partition(),
              result.getRecordMetadata().offset());
        } else {
          log.error("Failed to publish UserCreatedEvent. EventId: {}, Error: {}",
              event.eventId(), ex.getMessage(), ex);
        }
      });

      future.get(timeoutSeconds, TimeUnit.SECONDS);
      return true;
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error publishing UserCreatedEvent for userId: {}, eventId: {}",
          event.userId(), event.eventId(), e);
      return false;
    }
  }

  @Override
  public boolean publishUserUpdated(UserUpdatedEvent event) {
    log.info("Publishing UserUpdatedEvent for userId: {}, eventId: {}",
        event.userId(), event.eventId());
    try {
      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          userUpdatedTopic,
          event.userId(),
          event);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("UserUpdatedEvent published successfully. " +
              "EventId: {}, Partition: {}, Offset: {}",
              event.eventId(),
              result.getRecordMetadata().partition(),
              result.getRecordMetadata().offset());
        } else {
          log.error("Failed to publish UserUpdatedEvent. EventId: {}",
              event.eventId(), ex);
        }
      });

      future.get(timeoutSeconds, TimeUnit.SECONDS);
      return true;

    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error publishing UserUpdatedEvent for userId: {}",
          event.userId(), e);
      return false;
    }
  }

  @Override
  public boolean publishUserDeleted(UserDeletedEvent event) {
    log.info("Publishing UserDeletedEvent for userId: {}, eventId: {}",
        event.userId(), event.eventId());

    try {
      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          userDeletedTopic,
          event.userId(),
          event);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("UserDeletedEvent published successfully. " +
              "EventId: {}, Partition: {}, Offset: {}",
              event.eventId(),
              result.getRecordMetadata().partition(),
              result.getRecordMetadata().offset());
        } else {
          log.error("Failed to publish UserDeletedEvent. EventId: {}",
              event.eventId(), ex);
        }
      });

      future.get(timeoutSeconds, TimeUnit.SECONDS);
      return true;

    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error publishing UserDeletedEvent for userId: {}",
          event.userId(), e);
      return false;
    }
  }

  @Override
  public void publishPasswordChanged(PasswordChangedEvent event) {
    log.info("Publishing PasswordChangedEvent for userId: {}, reason: {}", 
        event.userId(), event.changeReason());

    try {
      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          passwordChangedTopic,
          event.userId(),
          event);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("PasswordChangedEvent published successfully. Topic: {}, Partition: {}, Offset: {}",
              result.getRecordMetadata().topic(),
              result.getRecordMetadata().partition(),
              result.getRecordMetadata().offset());
        } else {
          log.error("Failed to publish PasswordChangedEvent for userId: {}. Error: {}",
              event.userId(), ex.getMessage(), ex);
        }
      });

      future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error publishing PasswordChangedEvent for userId: {}", event.userId(), e);
      // Non-blocking: password change should succeed even if event publishing fails
    }
  }

  @Override
  public void publishAccountActivated(AccountActivatedEvent event) {
    log.info("Publishing AccountActivatedEvent for userId: {}", event.userId());

    try {
      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          accountActivatedTopic,
          event.userId(),
          event);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("AccountActivatedEvent published successfully. Topic: {}, Partition: {}, Offset: {}",
              result.getRecordMetadata().topic(),
              result.getRecordMetadata().partition(),
              result.getRecordMetadata().offset());
        } else {
          log.error("Failed to publish AccountActivatedEvent for userId: {}. Error: {}",
              event.userId(), ex.getMessage(), ex);
        }
      });

      future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error publishing AccountActivatedEvent for userId: {}", event.userId(), e);
      // Non-blocking: activation should succeed even if event publishing fails
    }
  }

  @Override
  public void publishTwoFactorEnabled(TwoFactorEnabledEvent event) {
    log.info("Publishing TwoFactorEnabledEvent for userId: {}, method: {}", 
        event.userId(), event.method());

    try {
      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          twoFactorEnabledTopic,
          event.userId(),
          event);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("TwoFactorEnabledEvent published successfully. Topic: {}, Partition: {}, Offset: {}",
              result.getRecordMetadata().topic(),
              result.getRecordMetadata().partition(),
              result.getRecordMetadata().offset());
        } else {
          log.error("Failed to publish TwoFactorEnabledEvent for userId: {}. Error: {}",
              event.userId(), ex.getMessage(), ex);
        }
      });

      future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error publishing TwoFactorEnabledEvent for userId: {}", event.userId(), e);
      // Non-blocking: 2FA enable should succeed even if event publishing fails
    }
  }

  @Override
  public void publishTwoFactorDisabled(TwoFactorDisabledEvent event) {
    log.info("Publishing TwoFactorDisabledEvent for userId: {}", event.userId());

    try {
      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          twoFactorDisabledTopic,
          event.userId(),
          event);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("TwoFactorDisabledEvent published successfully. Topic: {}, Partition: {}, Offset: {}",
              result.getRecordMetadata().topic(),
              result.getRecordMetadata().partition(),
              result.getRecordMetadata().offset());
        } else {
          log.error("Failed to publish TwoFactorDisabledEvent for userId: {}. Error: {}",
              event.userId(), ex.getMessage(), ex);
        }
      });

      future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error publishing TwoFactorDisabledEvent for userId: {}", event.userId(), e);
      // Non-blocking: 2FA disable should succeed even if event publishing fails
    }
  }
}
