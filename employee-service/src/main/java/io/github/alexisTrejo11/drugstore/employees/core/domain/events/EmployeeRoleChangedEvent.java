package io.github.alexisTrejo11.drugstore.employees.core.domain.events;

/**
 * Event published when an employee is promoted or their role changes
 */
public class EmployeeRoleChangedEvent extends EmployeeDomainEvent {

  private final String employeeNumber;
  private final String previousRole;
  private final String newRole;
  private final String reason;
  private final String approvedBy;

  public EmployeeRoleChangedEvent(String eventId, String employeeId, String employeeNumber,
      String previousRole, String newRole, String reason, String approvedBy) {
    super(eventId, employeeId);
    this.employeeNumber = employeeNumber;
    this.previousRole = previousRole;
    this.newRole = newRole;
    this.reason = reason;
    this.approvedBy = approvedBy;
  }

  @Override
  public String getEventType() {
    return "EMPLOYEE_ROLE_CHANGED";
  }

  public String getEmployeeNumber() {
    return employeeNumber;
  }

  public String getPreviousRole() {
    return previousRole;
  }

  public String getNewRole() {
    return newRole;
  }

  public String getReason() {
    return reason;
  }

  public String getApprovedBy() {
    return approvedBy;
  }

  @Override
  public String toString() {
    return "EmployeeRoleChangedEvent{" +
        "employeeId='" + getEmployeeId() + '\'' +
        ", employeeNumber='" + employeeNumber + '\'' +
        ", previousRole='" + previousRole + '\'' +
        ", newRole='" + newRole + '\'' +
        ", reason='" + reason + '\'' +
        '}';
  }
}
