package io.github.alexisTrejo11.drugstore.employees.core.domain.events;

import java.time.LocalDate;

/**
 * Event published when an employee is terminated
 */
public class EmployeeTerminatedEvent extends EmployeeDomainEvent {

  private final String employeeNumber;
  private final String firstName;
  private final String lastName;
  private final LocalDate terminationDate;
  private final String reason;
  private final String terminatedBy;

  public EmployeeTerminatedEvent(String eventId, String employeeId, String employeeNumber,
      String firstName, String lastName, LocalDate terminationDate,
      String reason, String terminatedBy) {
    super(eventId, employeeId);
    this.employeeNumber = employeeNumber;
    this.firstName = firstName;
    this.lastName = lastName;
    this.terminationDate = terminationDate;
    this.reason = reason;
    this.terminatedBy = terminatedBy;
  }

  @Override
  public String getEventType() {
    return "EMPLOYEE_TERMINATED";
  }

  public String getEmployeeNumber() {
    return employeeNumber;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public LocalDate getTerminationDate() {
    return terminationDate;
  }

  public String getReason() {
    return reason;
  }

  public String getTerminatedBy() {
    return terminatedBy;
  }

  @Override
  public String toString() {
    return "EmployeeTerminatedEvent{" +
        "employeeId='" + getEmployeeId() + '\'' +
        ", employeeNumber='" + employeeNumber + '\'' +
        ", name='" + firstName + " " + lastName + '\'' +
        ", terminationDate=" + terminationDate +
        ", reason='" + reason + '\'' +
        '}';
  }
}
