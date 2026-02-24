package io.github.alexisTrejo11.drugstore.payments.core.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base abstract class for all domain events.
 * Framework-agnostic, can be adapted to any event publisher mechanism.
 */
public abstract class DomainEvent {
  private final String eventId;
  private final LocalDateTime occurredOn;
  private final String eventType;

  protected DomainEvent(String eventType) {
    this.eventId = UUID.randomUUID().toString();
    this.occurredOn = LocalDateTime.now();
    this.eventType = eventType;
  }

  public String getEventId() {
    return eventId;
  }

  public LocalDateTime getOccurredOn() {
    return occurredOn;
  }

  public String getEventType() {
    return eventType;
  }

  /**
   * Returns the aggregate ID that this event relates to.
   */
  public abstract String getAggregateId();

  @Override
  public String toString() {
    return String.format("%s{eventId='%s', occurredOn=%s, aggregateId='%s'}",
        eventType, eventId, occurredOn, getAggregateId());
  }
}
