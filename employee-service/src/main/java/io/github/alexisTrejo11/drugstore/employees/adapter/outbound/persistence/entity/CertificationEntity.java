package io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing an employee certification
 */
@Entity
@Table(name = "employee_certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String licenseNumber;

  @Column(nullable = false)
  private String issuingAuthority;

  @Column(nullable = false)
  private LocalDate issueDate;

  @Column(nullable = false)
  private LocalDate expirationDate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CertificationTypeEnum type;

  @Column(name = "employee_id", nullable = false)
  private String employeeId;

  public enum CertificationTypeEnum {
    PHARMACIST_LICENSE,
    PHARMACY_TECHNICIAN_CERTIFICATION,
    CPR_CERTIFICATION,
    FIRST_AID_CERTIFICATION,
    IMMUNIZATION_CERTIFICATION,
    OTHER
  }
}
