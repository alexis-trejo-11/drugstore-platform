package io.github.alexisTrejo11.drugstore.employees.core.domain.events;

import java.time.LocalDateTime;

/**
 * Base class for all employee domain events
 */
public abstract class EmployeeDomainEvent {

  private final String eventId;
  private final LocalDateTime occurredOn;
  private final String employeeId;

  protected EmployeeDomainEvent(String eventId, String employeeId) {
    this.eventId = eventId != null ? eventId : java.util.UUID.randomUUID().toString();
    this.occurredOn = LocalDateTime.now();
    this.employeeId = employeeId;
  }

  public String getEventId() {
    return eventId;
  }

  public LocalDateTime getOccurredOn() {
    return occurredOn;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public abstract String getEventType();
}
