package io.github.alexisTrejo11.drugstore.users.user.adapter.output.messaging.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.alexisTrejo11.drugstore.users.user.adapter.output.messaging.kafka.handler.UserCreatedEventHandler;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.messaging.kafka.handler.UserDeletedEventHandler;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.messaging.kafka.handler.UserUpdatedEventHandler;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.events.UserCreatedEvent;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.events.UserDeletedEvent;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.events.UserUpdateEvent;
import lombok.RequiredArgsConstructor;

/**
 * Consumer for user events from Kafka
 * <p>
 * This class listens to Kafka topics and delegates processing
 * to the corresponding handlers.
 *
 * @KafkaListener: Annotation that marks a method as a Kafka listener
 *                 - topics: List of topics to listen to
 *                 - groupId: Consumer group
 *                 - containerFactory: Container configuration
 *                 <p>
 *                 Acknowledgment: Allows manually confirming that a message was
 *                 processed
 *                 - acknowledge(): Confirms the message (commit offset)
 *                 - If not called, the message will be reprocessed
 */
@Component
@RequiredArgsConstructor
public class UserEventConsumer {
  private final UserCreatedEventHandler userCreatedEventHandler;
  private final UserUpdatedEventHandler userUpdatedEventHandler;
  private final UserDeletedEventHandler userDeletedEventHandler;
  private final ObjectMapper objectMapper;
  private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserEventConsumer.class);

  @KafkaListener(topics = "${kafka.topics.user.created}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
  public void consumeUserCreated(
      @Payload String payload,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset,
      Acknowledgment acknowledgment) {
    log.info("Received UserCreatedEvent from partition: {}, offset: {}", partition, offset);
    try {
      UserCreatedEvent event = objectMapper.readValue(payload, UserCreatedEvent.class);

      userCreatedEventHandler.handle(event);

      // Confirmar procesamiento exitoso (commit offset)
      acknowledgment.acknowledge();

      log.info("UserCreatedEvent processed and acknowledged. EventId: {}, UserId: {}",
          event.getEventId(), event.getUserId());

    } catch (Exception e) {
      log.error("Error consuming UserCreatedEvent from partition: {}, offset: {}",
          partition, offset, e);

      // NO hacer acknowledge() - el mensaje será reprocesado
      // TODO: Implementar lógica de retry o DLQ
      throw new RuntimeException("Failed to process UserCreatedEvent", e);
    }
  }

  @KafkaListener(topics = "${kafka.topics.user.updated}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
  public void consumeUserUpdated(
      @Payload String payload,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset,
      Acknowledgment acknowledgment) {
    log.info("Received UserUpdateEvent from partition: {}, offset: {}", partition, offset);
    try {
      UserUpdateEvent event = objectMapper.readValue(payload, UserUpdateEvent.class);

      userUpdatedEventHandler.handle(event);

      // Confirm successful processing (commit offset)
      acknowledgment.acknowledge();

      log.info("UserUpdateEvent processed and acknowledged. EventId: {}, UserId: {}, UpdateType: {}",
          event.getEventId(), event.getUserId(), event.getUpdateType());

    } catch (Exception e) {
      log.error("Error consuming UserUpdateEvent from partition: {}, offset: {}",
          partition, offset, e);

      // DO NOT acknowledge() - the message will be reprocessed
      // TODO: Implement retry logic or DLQ
      throw new RuntimeException("Failed to process UserUpdateEvent", e);
    }
  }

  @KafkaListener(topics = "${kafka.topics.user.deleted}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
  public void consumeUserDeleted(
      @Payload String payload,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset,
      Acknowledgment acknowledgment) {
    log.info("Received UserDeletedEvent from partition: {}, offset: {}", partition, offset);
    try {
      UserDeletedEvent event = objectMapper.readValue(payload, UserDeletedEvent.class);

      userDeletedEventHandler.handle(event);

      // Confirm successful processing (commit offset)
      acknowledgment.acknowledge();

      log.info("UserDeletedEvent processed and acknowledged. EventId: {}, UserId: {}, Email: {}",
          event.getEventId(), event.getUserId(), event.getEmail());

    } catch (Exception e) {
      log.error("Error consuming UserDeletedEvent from partition: {}, offset: {}",
          partition, offset, e);

      // DO NOT acknowledge() - the message will be reprocessed
      // TODO: Implement retry logic or DLQ
      throw new RuntimeException("Failed to process UserDeletedEvent", e);
    }
  }
}
