package io.github.alexisTrejo11.drugstore.employees.core.domain.exception;

/**
 * Exception thrown when an employee attempts an unauthorized operation
 */
public class UnauthorizedEmployeeOperationException extends EmployeeDomainException {

  public UnauthorizedEmployeeOperationException(String operation, String reason) {
    super(String.format("Unauthorized operation '%s': %s", operation, reason));
  }

  public UnauthorizedEmployeeOperationException(String message) {
    super(message);
  }
}
