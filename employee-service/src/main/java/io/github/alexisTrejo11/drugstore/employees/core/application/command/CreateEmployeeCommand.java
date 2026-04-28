package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.ContactInfo;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeNumber;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeRole;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeType;

/**
 * Command for creating a new employee
 */
public record CreateEmployeeCommand(
    EmployeeNumber employeeNumber,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    ContactInfo contactInfo,
    Map<String, Object> workdaySchedule,
    EmployeeRole role,
    EmployeeType employeeType,
    String department,
    String storeId,
    LocalDate hireDate,
    BigDecimal hourlyRate,
    Integer weeklyHours,
    String createdBy) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private EmployeeNumber employeeNumber;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private ContactInfo contactInfo;
    private Map<String, Object> workdaySchedule;
    private EmployeeRole role;
    private EmployeeType employeeType;
    private String department;
    private String storeId;
    private LocalDate hireDate;
    private BigDecimal hourlyRate;
    private Integer weeklyHours;
    private String createdBy;

    public Builder employeeNumber(EmployeeNumber employeeNumber) {
      this.employeeNumber = employeeNumber;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder dateOfBirth(LocalDate dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
      return this;
    }

    public Builder contactInfo(ContactInfo contactInfo) {
      this.contactInfo = contactInfo;
      return this;
    }

    public Builder workdaySchedule(Map<String, Object> workdaySchedule) {
      this.workdaySchedule = workdaySchedule;
      return this;
    }

    public Builder role(EmployeeRole role) {
      this.role = role;
      return this;
    }

    public Builder employeeType(EmployeeType employeeType) {
      this.employeeType = employeeType;
      return this;
    }

    public Builder department(String department) {
      this.department = department;
      return this;
    }

    public Builder storeId(String storeId) {
      this.storeId = storeId;
      return this;
    }

    public Builder hireDate(LocalDate hireDate) {
      this.hireDate = hireDate;
      return this;
    }

    public Builder hourlyRate(BigDecimal hourlyRate) {
      this.hourlyRate = hourlyRate;
      return this;
    }

    public Builder weeklyHours(Integer weeklyHours) {
      this.weeklyHours = weeklyHours;
      return this;
    }

    public Builder createdBy(String createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public CreateEmployeeCommand build() {
      return new CreateEmployeeCommand(
          employeeNumber,
          firstName,
          lastName,
          dateOfBirth,
          contactInfo,
          workdaySchedule,
          role,
          employeeType,
          department,
          storeId,
          hireDate,
          hourlyRate,
          weeklyHours,
          createdBy);
    }
  }
}
