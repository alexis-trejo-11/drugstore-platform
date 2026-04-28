package io.github.alexisTrejo11.drugstore.employees.core.domain.exception;

/**
 * Exception thrown when business rules are violated
 */
public class EmployeeBusinessRuleException extends EmployeeDomainException {

  public EmployeeBusinessRuleException(String message) {
    super(message);
  }

  public EmployeeBusinessRuleException(String message, Throwable cause) {
    super(message, cause);
  }
}
