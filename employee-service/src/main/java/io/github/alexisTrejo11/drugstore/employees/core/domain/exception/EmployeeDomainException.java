package io.github.alexisTrejo11.drugstore.employees.core.domain.exception;

/**
 * Base exception for all employee domain-related exceptions
 */
public class EmployeeDomainException extends RuntimeException {

  public EmployeeDomainException(String message) {
    super(message);
  }

  public EmployeeDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
