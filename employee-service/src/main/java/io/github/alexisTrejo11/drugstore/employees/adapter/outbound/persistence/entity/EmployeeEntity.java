package io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity for Employee
 */
@Entity
@Table(name = "employees", indexes = {
    @Index(name = "idx_employee_number", columnList = "employeeNumber", unique = true),
    @Index(name = "idx_employee_status", columnList = "status"),
    @Index(name = "idx_employee_role", columnList = "role"),
    @Index(name = "idx_employee_store", columnList = "storeId"),
    @Index(name = "idx_employee_deleted", columnList = "deletedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEntity {

  @Id
  private String id;

  @Column(nullable = false, unique = true, length = 20)
  private String employeeNumber;

  // Personal Information
  @Column(nullable = false, length = 100)
  private String firstName;

  @Column(nullable = false, length = 100)
  private String lastName;

  @Column(nullable = false)
  private LocalDate dateOfBirth;

  @Embedded
  private AddressEmbeddable address;

  @Embedded
  private ContactInfoEmbeddable contactInfo;

  // Employment Details
  @Column(nullable = false, length = 50)
  @Enumerated(EnumType.STRING)
  private EmployeeRoleEnum role;

  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private EmployeeTypeEnum employeeType;

  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private EmployeeStatusEnum status;

  @Column(length = 100)
  private String department;

  @Column(length = 50)
  private String storeId;

  @Column(nullable = false)
  private LocalDate hireDate;

  private LocalDate terminationDate;

  // Certifications
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JoinColumn(name = "employee_id")
  private List<CertificationEntity> certifications = new ArrayList<>();

  // Compensation
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal hourlyRate;

  @Column(nullable = false)
  private Integer weeklyHours;

  // Audit fields
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @Column(updatable = false, length = 100)
  private String createdBy;

  @Column(length = 100)
  private String lastModifiedBy;

  // Soft delete
  private LocalDateTime deletedAt;

  // Enums
  public enum EmployeeRoleEnum {
    PHARMACIST,
    PHARMACY_TECHNICIAN,
    STORE_MANAGER,
    ASSISTANT_MANAGER,
    CASHIER,
    INVENTORY_CLERK,
    DELIVERY_DRIVER,
    CUSTOMER_SERVICE_REP,
    JANITOR
  }

  public enum EmployeeTypeEnum {
    FULL_TIME,
    PART_TIME,
    CONTRACTOR,
    INTERN,
    SEASONAL
  }

  public enum EmployeeStatusEnum {
    ACTIVE,
    INACTIVE,
    ON_LEAVE,
    SUSPENDED,
    TERMINATED
  }

  // Audit methods
  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  // Business methods
  public void markAsDeleted() {
    this.deletedAt = LocalDateTime.now();
    this.status = EmployeeStatusEnum.TERMINATED;
  }

  public void markAsRestored() {
    this.deletedAt = null;
    this.status = EmployeeStatusEnum.ACTIVE;
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }
}
