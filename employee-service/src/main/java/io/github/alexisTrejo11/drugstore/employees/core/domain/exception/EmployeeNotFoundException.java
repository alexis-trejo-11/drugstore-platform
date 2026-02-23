package io.github.alexisTrejo11.drugstore.employees.core.domain.exception;

/**
 * Exception thrown when an employee is not found
 */
public class EmployeeNotFoundException extends EmployeeDomainException {

  public EmployeeNotFoundException(String employeeId) {
    super(String.format("Employee not found with ID: %s", employeeId));
  }

  public EmployeeNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
