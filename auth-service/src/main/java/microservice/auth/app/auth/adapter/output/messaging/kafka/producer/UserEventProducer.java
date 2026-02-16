package microservice.auth.app.auth.adapter.output.messaging.kafka.producer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import microservice.auth.app.auth.core.application.events.UserLoginEvent;
import microservice.auth.app.auth.core.application.events.UserRegisteredEvent;
import microservice.auth.app.auth.core.domain.event.user.UserCreatedEvent;
import microservice.auth.app.auth.core.domain.event.user.UserDeletedEvent;
import microservice.auth.app.auth.core.domain.event.user.UserUpdatedEvent;
import microservice.auth.app.auth.core.ports.input.UserEventPublisher;

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

  @Value("${kafka.producer.timeout-seconds:10}")
  private long timeoutSeconds;

  @Autowired
  public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void publishUserRegistered(UserRegisteredEvent event) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void publishUserLogin(UserLoginEvent event) {
    throw new UnsupportedOperationException("Not supported yet.");
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
}
