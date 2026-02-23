package io.github.alexisTrejo11.drugstore.employees.core.domain.exception;

/**
 * Exception thrown when an employee's certification is expired or invalid
 */
public class InvalidCertificationException extends EmployeeDomainException {

  public InvalidCertificationException(String message) {
    super(message);
  }

  public InvalidCertificationException(String licenseNumber, String reason) {
    super(String.format("Invalid certification for license %s: %s", licenseNumber, reason));
  }
}
