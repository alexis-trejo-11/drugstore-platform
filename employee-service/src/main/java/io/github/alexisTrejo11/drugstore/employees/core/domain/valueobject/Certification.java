package io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Value object representing professional certification (required for
 * pharmacists
 * and pharmacy technicians)
 */
public class Certification implements Serializable {

  private final String licenseNumber;
  private final String issuingAuthority;
  private final LocalDate issueDate;
  private final LocalDate expirationDate;
  private final CertificationType type;

  private Certification(String licenseNumber, String issuingAuthority, LocalDate issueDate,
      LocalDate expirationDate, CertificationType type) {
    this.licenseNumber = licenseNumber;
    this.issuingAuthority = issuingAuthority;
    this.issueDate = issueDate;
    this.expirationDate = expirationDate;
    this.type = type;
  }

  public static Certification of(String licenseNumber, String issuingAuthority,
      LocalDate issueDate, LocalDate expirationDate, CertificationType type) {

    if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("License number cannot be null or empty");
    }
    if (issuingAuthority == null || issuingAuthority.trim().isEmpty()) {
      throw new IllegalArgumentException("Issuing authority cannot be null or empty");
    }
    if (issueDate == null) {
      throw new IllegalArgumentException("Issue date cannot be null");
    }
    if (expirationDate == null) {
      throw new IllegalArgumentException("Expiration date cannot be null");
    }
    if (expirationDate.isBefore(issueDate)) {
      throw new IllegalArgumentException("Expiration date cannot be before issue date");
    }
    if (type == null) {
      throw new IllegalArgumentException("Certification type cannot be null");
    }

    return new Certification(licenseNumber, issuingAuthority, issueDate, expirationDate, type);
  }

  public boolean isValid() {
    return !isExpired();
  }

  public boolean isExpired() {
    return LocalDate.now().isAfter(expirationDate);
  }

  public boolean isExpiringSoon(int daysThreshold) {
    return LocalDate.now().plusDays(daysThreshold).isAfter(expirationDate);
  }

  public long daysUntilExpiration() {
    return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
  }

  public String getLicenseNumber() {
    return licenseNumber;
  }

  public String getIssuingAuthority() {
    return issuingAuthority;
  }

  public LocalDate getIssueDate() {
    return issueDate;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public CertificationType getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Certification that = (Certification) o;
    return Objects.equals(licenseNumber, that.licenseNumber) &&
        Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(licenseNumber, type);
  }

  @Override
  public String toString() {
    return "Certification{" +
        "type=" + type +
        ", licenseNumber='" + licenseNumber + '\'' +
        ", expirationDate=" + expirationDate +
        ", isValid=" + isValid() +
        '}';
  }

  public enum CertificationType {
    PHARMACIST_LICENSE,
    PHARMACY_TECHNICIAN_LICENSE,
    SPECIALIZED_TRAINING,
    CPR_CERTIFICATION,
    CONTROLLED_SUBSTANCE_LICENSE,
    IMMUNIZATION_CERTIFICATION
  }
}
