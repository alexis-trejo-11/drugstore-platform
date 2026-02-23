package io.github.alexisTrejo11.drugstore.employees.core.domain.exception;

/**
 * Exception thrown when an employee already exists (duplicate employee number
 * or
 * email)
 */
public class EmployeeAlreadyExistsException extends EmployeeDomainException {

  public EmployeeAlreadyExistsException(String identifier) {
    super(String.format("Employee already exists with identifier: %s", identifier));
  }

  public EmployeeAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
