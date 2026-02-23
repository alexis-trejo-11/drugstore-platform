package io.github.alexisTrejo11.drugstore.employees.core.domain.events;

/**
 * Event published when an employee's status changes
 */
public class EmployeeStatusChangedEvent extends EmployeeDomainEvent {

  private final String employeeNumber;
  private final String previousStatus;
  private final String newStatus;
  private final String reason;
  private final String changedBy;

  public EmployeeStatusChangedEvent(String eventId, String employeeId, String employeeNumber,
      String previousStatus, String newStatus, String reason, String changedBy) {
    super(eventId, employeeId);
    this.employeeNumber = employeeNumber;
    this.previousStatus = previousStatus;
    this.newStatus = newStatus;
    this.reason = reason;
    this.changedBy = changedBy;
  }

  @Override
  public String getEventType() {
    return "EMPLOYEE_STATUS_CHANGED";
  }

  public String getEmployeeNumber() {
    return employeeNumber;
  }

  public String getPreviousStatus() {
    return previousStatus;
  }

  public String getNewStatus() {
    return newStatus;
  }

  public String getReason() {
    return reason;
  }

  public String getChangedBy() {
    return changedBy;
  }

  @Override
  public String toString() {
    return "EmployeeStatusChangedEvent{" +
        "employeeId='" + getEmployeeId() + '\'' +
        ", employeeNumber='" + employeeNumber + '\'' +
        ", previousStatus='" + previousStatus + '\'' +
        ", newStatus='" + newStatus + '\'' +
        ", reason='" + reason + '\'' +
        '}';
  }
}
