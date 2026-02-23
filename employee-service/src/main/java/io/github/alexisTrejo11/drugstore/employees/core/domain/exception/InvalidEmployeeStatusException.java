package io.github.alexisTrejo11.drugstore.employees.core.domain.exception;

/**
 * Exception thrown when attempting to perform an operation that requires a
 * different employee status
 */
public class InvalidEmployeeStatusException extends EmployeeDomainException {

  public InvalidEmployeeStatusException(String currentStatus, String requiredStatus) {
    super(String.format("Invalid employee status. Current: %s, Required: %s", currentStatus, requiredStatus));
  }

  public InvalidEmployeeStatusException(String message) {
    super(message);
  }
}
