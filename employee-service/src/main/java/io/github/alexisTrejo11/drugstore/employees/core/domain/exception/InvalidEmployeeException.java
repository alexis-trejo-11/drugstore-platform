package io.github.alexisTrejo11.drugstore.employees.core.domain.exception;

/**
 * Exception thrown when employee data validation fails
 */
public class InvalidEmployeeException extends EmployeeDomainException {

  public InvalidEmployeeException(String message) {
    super(message);
  }

  public InvalidEmployeeException(String message, Throwable cause) {
    super(message, cause);
  }
}
