package io.github.alexisTrejo11.drugstore.employees.core.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.alexisTrejo11.drugstore.employees.core.domain.events.*;
import io.github.alexisTrejo11.drugstore.employees.core.domain.exception.*;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.*;

/**
 * Employee Aggregate Root
 * 
 * Represents an employee in the drugstore system with full business logic for
 * employee lifecycle management
 */
public class Employee {

  private static final Logger log = LoggerFactory.getLogger(Employee.class);

  // Identity
  private EmployeeId id;
  private EmployeeNumber employeeNumber;

  // Personal Information
  private String firstName;
  private String lastName;
  private LocalDate dateOfBirth;
  private ContactInfo contactInfo;

  // Workday Schedule (stored as JSON)
  private Map<String, Object> workdaySchedule;

  // Employment Details
  private EmployeeRole role;
  private EmployeeType employeeType;
  private EmployeeStatus status;
  private String department;
  private String storeId;
  private LocalDate hireDate;
  private LocalDate terminationDate;

  // Certifications (for roles requiring licenses)
  private List<Certification> certifications;

  // Compensation
  private BigDecimal hourlyRate;
  private Integer weeklyHours;

  // Audit
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String lastModifiedBy;

  // Domain Events
  private List<EmployeeDomainEvent> domainEvents;

  // Constructor
  public Employee() {
    this.certifications = new ArrayList<>();
    this.domainEvents = new ArrayList<>();
    this.status = EmployeeStatus.INACTIVE;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.workdaySchedule = new HashMap<>();
  }

  // Factory method for hiring new employee
  public static Employee hire(
      EmployeeNumber employeeNumber,
      String firstName,
      String lastName,
      LocalDate dateOfBirth,
      ContactInfo contactInfo,
      EmployeeRole role,
      EmployeeType employeeType,
      String department,
      String storeId,
      LocalDate hireDate,
      BigDecimal hourlyRate,
      Integer weeklyHours,
      String hiredBy) {

    log.info("Hiring new employee: {} {} - Role: {}", firstName, lastName, role);

    // Validations
    if (firstName == null || firstName.trim().isEmpty()) {
      throw new InvalidEmployeeException("First name cannot be null or empty");
    }
    if (lastName == null || lastName.trim().isEmpty()) {
      throw new InvalidEmployeeException("Last name cannot be null or empty");
    }
    if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now().minusYears(18))) {
      throw new InvalidEmployeeException("Employee must be at least 18 years old");
    }
    if (hireDate == null) {
      throw new InvalidEmployeeException("Hire date cannot be null");
    }
    if (hourlyRate == null || hourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
      throw new InvalidEmployeeException("Hourly rate must be greater than zero");
    }
    if (weeklyHours == null || weeklyHours <= 0) {
      throw new InvalidEmployeeException("Weekly hours must be greater than zero");
    }
    if (!employeeType.isValidWeeklyHours(weeklyHours)) {
      throw new InvalidEmployeeException(
          String.format("Weekly hours %d not valid for employment type %s", weeklyHours, employeeType));
    }

    Employee employee = new Employee();
    employee.id = EmployeeId.generate();
    employee.employeeNumber = employeeNumber;
    employee.firstName = firstName;
    employee.lastName = lastName;
    employee.dateOfBirth = dateOfBirth;
    employee.contactInfo = contactInfo;
    employee.role = role;
    employee.employeeType = employeeType;
    employee.status = EmployeeStatus.ACTIVE;
    employee.department = department;
    employee.storeId = storeId;
    employee.hireDate = hireDate;
    employee.hourlyRate = hourlyRate;
    employee.weeklyHours = weeklyHours;
    employee.createdBy = hiredBy;
    employee.lastModifiedBy = hiredBy;

    // Validate certifications for roles that require them
    if (role.requiresCertification()) {
      log.warn("Employee hired in role {} which requires certification. Certification must be added immediately.",
          role);
    }

    // Register domain event
    employee.registerEvent(new EmployeeHiredEvent(
        null,
        employee.id.getValue(),
        employee.employeeNumber.getValue(),
        employee.firstName,
        employee.lastName,
        employee.contactInfo.getEmail(),
        employee.role.name(),
        employee.employeeType.name(),
        employee.hireDate,
        employee.storeId,
        employee.department));

    log.info("Employee hired successfully: {}", employee.employeeNumber);
    return employee;
  }

  // Business Method: Add or update certification
  public void addCertification(Certification certification) {
    log.info("Adding certification for employee {}: {}", employeeNumber, certification.getType());

    if (certification == null) {
      throw new InvalidEmployeeException("Certification cannot be null");
    }

    if (certification.isExpired()) {
      throw new InvalidCertificationException(certification.getLicenseNumber(), "Certification is expired");
    }

    // Remove existing certification of same type
    certifications.removeIf(c -> c.getType() == certification.getType());

    // Add new certification
    certifications.add(certification);
    this.updatedAt = LocalDateTime.now();

    registerEvent(new EmployeeCertificationUpdatedEvent(
        null,
        this.id.getValue(),
        this.employeeNumber.getValue(),
        certification.getType().name(),
        certification.getLicenseNumber(),
        certification.getExpirationDate(),
        certifications.size() > 1));

    log.info("Certification added successfully for employee {}", employeeNumber);
  }

  // Business Method: Validate certifications
  public void validateCertifications() {
    log.debug("Validating certifications for employee {}", employeeNumber);

    if (role.requiresCertification()) {
      if (certifications.isEmpty()) {
        throw new InvalidCertificationException(
            String.format("Employee in role %s must have valid certification", role));
      }

      // Check for expired certifications
      List<Certification> expiredCerts = certifications.stream()
          .filter(Certification::isExpired)
          .toList();

      if (!expiredCerts.isEmpty()) {
        log.error("Employee {} has {} expired certifications", employeeNumber, expiredCerts.size());
        throw new InvalidCertificationException(
            String.format("Employee has %d expired certification(s)", expiredCerts.size()));
      }
    }
  }

  // Business Method: Change employee status
  public void changeStatus(EmployeeStatus newStatus, String reason, String changedBy) {
    log.info("Changing status for employee {} from {} to {}", employeeNumber, status, newStatus);

    if (newStatus == null) {
      throw new InvalidEmployeeException("New status cannot be null");
    }

    if (status == newStatus) {
      log.warn("Employee {} is already in status {}", employeeNumber, newStatus);
      return;
    }

    // Cannot change status of terminated employee
    if (status.isFinal()) {
      throw new InvalidEmployeeStatusException(
          String.format("Cannot change status of terminated employee: %s", employeeNumber));
    }

    EmployeeStatus previousStatus = this.status;
    this.status = newStatus;
    this.updatedAt = LocalDateTime.now();
    this.lastModifiedBy = changedBy;

    registerEvent(new EmployeeStatusChangedEvent(
        null,
        this.id.getValue(),
        this.employeeNumber.getValue(),
        previousStatus.name(),
        newStatus.name(),
        reason,
        changedBy));

    log.info("Employee {} status changed successfully to {}", employeeNumber, newStatus);
  }

  // Business Method: Suspend employee
  public void suspend(String reason, String suspendedBy) {
    log.info("Suspending employee {}: {}", employeeNumber, reason);

    if (status == EmployeeStatus.TERMINATED) {
      throw new InvalidEmployeeStatusException("Cannot suspend a terminated employee");
    }

    changeStatus(EmployeeStatus.SUSPENDED, reason, suspendedBy);
  }

  // Business Method: Activate employee
  public void activate(String reason, String activatedBy) {
    log.info("Activating employee {}", employeeNumber);

    if (status == EmployeeStatus.TERMINATED) {
      throw new InvalidEmployeeStatusException("Cannot activate a terminated employee");
    }

    // Validate certifications before activation
    if (role.requiresCertification()) {
      validateCertifications();
    }

    changeStatus(EmployeeStatus.ACTIVE, reason, activatedBy);
  }

  // Business Method: Put employee on leave
  public void putOnLeave(String reason, String approvedBy) {
    log.info("Putting employee {} on leave", employeeNumber);

    if (status != EmployeeStatus.ACTIVE) {
      throw new InvalidEmployeeStatusException("Only active employees can be put on leave");
    }

    changeStatus(EmployeeStatus.ON_LEAVE, reason, approvedBy);
  }

  // Business Method: Terminate employee
  public void terminate(LocalDate terminationDate, String reason, String terminatedBy) {
    log.info("Terminating employee {}: {}", employeeNumber, reason);

    if (status == EmployeeStatus.TERMINATED) {
      throw new InvalidEmployeeStatusException("Employee is already terminated");
    }

    if (terminationDate == null) {
      terminationDate = LocalDate.now();
    }

    if (terminationDate.isBefore(hireDate)) {
      throw new InvalidEmployeeException("Termination date cannot be before hire date");
    }

    this.status = EmployeeStatus.TERMINATED;
    this.terminationDate = terminationDate;
    this.updatedAt = LocalDateTime.now();
    this.lastModifiedBy = terminatedBy;

    registerEvent(new EmployeeTerminatedEvent(
        null,
        this.id.getValue(),
        this.employeeNumber.getValue(),
        this.firstName,
        this.lastName,
        terminationDate,
        reason,
        terminatedBy));

    log.info("Employee {} terminated successfully", employeeNumber);
  }

  // Business Method: Change role (promotion/demotion)
  public void changeRole(EmployeeRole newRole, String reason, String approvedBy) {
    log.info("Changing role for employee {} from {} to {}", employeeNumber, role, newRole);

    if (newRole == null) {
      throw new InvalidEmployeeException("New role cannot be null");
    }

    if (role == newRole) {
      log.warn("Employee {} already has role {}", employeeNumber, newRole);
      return;
    }

    // Validate certifications for new role
    if (newRole.requiresCertification()) {
      if (certifications.isEmpty()) {
        throw new InvalidCertificationException(
            String.format("Role %s requires certification but none found", newRole));
      }
      validateCertifications();
    }

    EmployeeRole previousRole = this.role;
    this.role = newRole;
    this.updatedAt = LocalDateTime.now();
    this.lastModifiedBy = approvedBy;

    registerEvent(new EmployeeRoleChangedEvent(
        null,
        this.id.getValue(),
        this.employeeNumber.getValue(),
        previousRole.name(),
        newRole.name(),
        reason,
        approvedBy));

    log.info("Employee {} role changed successfully to {}", employeeNumber, newRole);
  }

  // Business Method: Update compensation
  public void updateCompensation(BigDecimal newHourlyRate, Integer newWeeklyHours, String updatedBy) {
    log.info("Updating compensation for employee {}", employeeNumber);

    if (newHourlyRate != null && newHourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
      throw new InvalidEmployeeException("Hourly rate must be greater than zero");
    }

    if (newWeeklyHours != null && !employeeType.isValidWeeklyHours(newWeeklyHours)) {
      throw new InvalidEmployeeException(
          String.format("Weekly hours %d not valid for employment type %s", newWeeklyHours, employeeType));
    }

    if (newHourlyRate != null) {
      this.hourlyRate = newHourlyRate;
    }
    if (newWeeklyHours != null) {
      this.weeklyHours = newWeeklyHours;
    }

    this.updatedAt = LocalDateTime.now();
    this.lastModifiedBy = updatedBy;

    log.info("Compensation updated for employee {}", employeeNumber);
  }

  // Business Method: Transfer to another store/department
  public void transfer(String newStoreId, String newDepartment, String approvedBy) {
    log.info("Transferring employee {} to store: {}, department: {}", employeeNumber, newStoreId, newDepartment);

    if (status != EmployeeStatus.ACTIVE) {
      throw new InvalidEmployeeStatusException("Only active employees can be transferred");
    }

    if (newStoreId != null) {
      this.storeId = newStoreId;
    }
    if (newDepartment != null) {
      this.department = newDepartment;
    }

    this.updatedAt = LocalDateTime.now();
    this.lastModifiedBy = approvedBy;

    log.info("Employee {} transferred successfully", employeeNumber);
  }

  // Business Method: Check if employee can work
  public boolean canWork() {
    if (!status.canWork()) {
      return false;
    }

    // If role requires certification, check validity
    if (role.requiresCertification()) {
      return certifications.stream().anyMatch(Certification::isValid);
    }

    return true;
  }

  // Business Method: Check if certifications are expiring soon
  public List<Certification> getCertificationsExpiringSoon(int daysThreshold) {
    return certifications.stream()
        .filter(cert -> cert.isExpiringSoon(daysThreshold))
        .toList();
  }

  // Business Method: Calculate years of service
  public int getYearsOfService() {
    LocalDate endDate = terminationDate != null ? terminationDate : LocalDate.now();
    return (int) java.time.temporal.ChronoUnit.YEARS.between(hireDate, endDate);
  }

  // Business Method: Get full name
  public String getFullName() {
    return firstName + " " + lastName;
  }

  // Domain Events Management
  private void registerEvent(EmployeeDomainEvent event) {
    if (domainEvents == null) {
      domainEvents = new ArrayList<>();
    }
    domainEvents.add(event);
  }

  public List<EmployeeDomainEvent> getDomainEvents() {
    return domainEvents != null ? Collections.unmodifiableList(domainEvents) : Collections.emptyList();
  }

  public void clearDomainEvents() {
    if (domainEvents != null) {
      domainEvents.clear();
    }
  }

  // Getters and Setters
  public EmployeeId getId() {
    return id;
  }

  public void setId(EmployeeId id) {
    this.id = id;
  }

  public EmployeeNumber getEmployeeNumber() {
    return employeeNumber;
  }

  public void setEmployeeNumber(EmployeeNumber employeeNumber) {
    this.employeeNumber = employeeNumber;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public ContactInfo getContactInfo() {
    return contactInfo;
  }

  public void setContactInfo(ContactInfo contactInfo) {
    this.contactInfo = contactInfo;
  }

  public EmployeeRole getRole() {
    return role;
  }

  public void setRole(EmployeeRole role) {
    this.role = role;
  }

  public EmployeeType getEmployeeType() {
    return employeeType;
  }

  public void setEmployeeType(EmployeeType employeeType) {
    this.employeeType = employeeType;
  }

  public EmployeeStatus getStatus() {
    return status;
  }

  public void setStatus(EmployeeStatus status) {
    this.status = status;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public String getStoreId() {
    return storeId;
  }

  public void setStoreId(String storeId) {
    this.storeId = storeId;
  }

  public LocalDate getHireDate() {
    return hireDate;
  }

  public void setHireDate(LocalDate hireDate) {
    this.hireDate = hireDate;
  }

  public LocalDate getTerminationDate() {
    return terminationDate;
  }

  public void setTerminationDate(LocalDate terminationDate) {
    this.terminationDate = terminationDate;
  }

  public List<Certification> getCertifications() {
    return certifications != null ? Collections.unmodifiableList(certifications) : Collections.emptyList();
  }

  public void setCertifications(List<Certification> certifications) {
    this.certifications = certifications != null ? new ArrayList<>(certifications) : new ArrayList<>();
  }

  public BigDecimal getHourlyRate() {
    return hourlyRate;
  }

  public void setHourlyRate(BigDecimal hourlyRate) {
    this.hourlyRate = hourlyRate;
  }

  public Integer getWeeklyHours() {
    return weeklyHours;
  }

  public void setWeeklyHours(Integer weeklyHours) {
    this.weeklyHours = weeklyHours;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public Map<String, Object> getWorkdaySchedule() {
    return workdaySchedule != null ? new HashMap<>(workdaySchedule) : new HashMap<>();
  }

  public void setWorkdaySchedule(Map<String, Object> workdaySchedule) {
    this.workdaySchedule = workdaySchedule != null ? new HashMap<>(workdaySchedule) : new HashMap<>();
  }

  @Override
  public String toString() {
    return "Employee{" +
        "employeeNumber=" + employeeNumber +
        ", name='" + getFullName() + '\'' +
        ", role=" + role +
        ", status=" + status +
        ", department='" + department + '\'' +
        ", hireDate=" + hireDate +
        '}';
  }
}
