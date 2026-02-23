package io.github.alexisTrejo11.drugstore.employees.core.domain.events;

import java.time.LocalDate;

/**
 * Event published when a new employee is hired
 */
public class EmployeeHiredEvent extends EmployeeDomainEvent {

  private final String employeeNumber;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String role;
  private final String employeeType;
  private final LocalDate hireDate;
  private final String storeId;
  private final String department;

  public EmployeeHiredEvent(String eventId, String employeeId, String employeeNumber,
      String firstName, String lastName, String email, String role,
      String employeeType, LocalDate hireDate, String storeId, String department) {
    super(eventId, employeeId);
    this.employeeNumber = employeeNumber;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.role = role;
    this.employeeType = employeeType;
    this.hireDate = hireDate;
    this.storeId = storeId;
    this.department = department;
  }

  @Override
  public String getEventType() {
    return "EMPLOYEE_HIRED";
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

  public String getEmail() {
    return email;
  }

  public String getRole() {
    return role;
  }

  public String getEmployeeType() {
    return employeeType;
  }

  public LocalDate getHireDate() {
    return hireDate;
  }

  public String getStoreId() {
    return storeId;
  }

  public String getDepartment() {
    return department;
  }

  @Override
  public String toString() {
    return "EmployeeHiredEvent{" +
        "employeeId='" + getEmployeeId() + '\'' +
        ", employeeNumber='" + employeeNumber + '\'' +
        ", name='" + firstName + " " + lastName + '\'' +
        ", role='" + role + '\'' +
        ", hireDate=" + hireDate +
        '}';
  }
}
