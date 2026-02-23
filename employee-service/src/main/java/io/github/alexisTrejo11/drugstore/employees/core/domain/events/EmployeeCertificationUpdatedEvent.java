package io.github.alexisTrejo11.drugstore.employees.core.domain.events;

import java.time.LocalDate;

/**
 * Event published when an employee's certification is updated or renewed
 */
public class EmployeeCertificationUpdatedEvent extends EmployeeDomainEvent {

  private final String employeeNumber;
  private final String certificationType;
  private final String licenseNumber;
  private final LocalDate expirationDate;
  private final boolean isRenewal;

  public EmployeeCertificationUpdatedEvent(String eventId, String employeeId, String employeeNumber,
      String certificationType, String licenseNumber, LocalDate expirationDate, boolean isRenewal) {
    super(eventId, employeeId);
    this.employeeNumber = employeeNumber;
    this.certificationType = certificationType;
    this.licenseNumber = licenseNumber;
    this.expirationDate = expirationDate;
    this.isRenewal = isRenewal;
  }

  @Override
  public String getEventType() {
    return "EMPLOYEE_CERTIFICATION_UPDATED";
  }

  public String getEmployeeNumber() {
    return employeeNumber;
  }

  public String getCertificationType() {
    return certificationType;
  }

  public String getLicenseNumber() {
    return licenseNumber;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public boolean isRenewal() {
    return isRenewal;
  }

  @Override
  public String toString() {
    return "EmployeeCertificationUpdatedEvent{" +
        "employeeId='" + getEmployeeId() + '\'' +
        ", employeeNumber='" + employeeNumber + '\'' +
        ", certificationType='" + certificationType + '\'' +
        ", licenseNumber='" + licenseNumber + '\'' +
        ", expirationDate=" + expirationDate +
        ", isRenewal=" + isRenewal +
        '}';
  }
}
