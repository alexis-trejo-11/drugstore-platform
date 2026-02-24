package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import io.github.alexisTrejo11.drugstore.employees.core.application.command.CreateEmployeeCommand;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeNumber;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeRole;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

/**
 * Request DTO for creating a new employee
 */
@Schema(description = "Create Employee Request DTO")
public record CreateEmployeeRequest(
    @NotNull @NotBlank @Schema(description = "Unique employee number", example = "EMP-001", required = true) String employeeNumber,

    @NotNull @NotBlank @Schema(description = "First name", example = "John", required = true) String firstName,

    @NotNull @NotBlank @Schema(description = "Last name", example = "Doe", required = true) String lastName,

    @NotNull @Past @Schema(description = "Date of birth (must be 18+ years old)", example = "1990-05-15", required = true) LocalDate dateOfBirth,

    @NotNull @Valid @Schema(description = "Contact information", required = true) ContactInfoRequest contactInfo,

    @Schema(description = "Workday schedule (JSON)") Map<String, Object> workdaySchedule,

    @NotNull @Schema(description = "Employee role", example = "PHARMACIST", required = true) EmployeeRole role,

    @NotNull @Schema(description = "Employment type", example = "FULL_TIME", required = true) EmployeeType employeeType,

    @NotNull @NotBlank @Schema(description = "Department name", example = "Pharmacy", required = true) String department,

    @NotNull @NotBlank @Schema(description = "Store identifier", example = "store-123", required = true) String storeId,

    @NotNull @Schema(description = "Hire date", example = "2024-01-15", required = true) LocalDate hireDate,

    @NotNull @DecimalMin(value = "0.01", message = "Hourly rate must be greater than zero") @Schema(description = "Hourly rate", example = "25.50", required = true) BigDecimal hourlyRate,

    @NotNull @Min(value = 1, message = "Weekly hours must be at least 1") @Schema(description = "Weekly hours", example = "40", required = true) Integer weeklyHours,

    @NotNull @NotBlank @Schema(description = "User who is creating the employee", example = "admin@drugstore.com", required = true) String createdBy) {

  public CreateEmployeeCommand toCommand() {
    return CreateEmployeeCommand.builder()
        .employeeNumber(EmployeeNumber.of(this.employeeNumber))
        .firstName(this.firstName)
        .lastName(this.lastName)
        .dateOfBirth(this.dateOfBirth)
        .contactInfo(this.contactInfo.toDomain())
        .workdaySchedule(this.workdaySchedule)
        .role(this.role)
        .employeeType(this.employeeType)
        .department(this.department)
        .storeId(this.storeId)
        .hireDate(this.hireDate)
        .hourlyRate(this.hourlyRate)
        .weeklyHours(this.weeklyHours)
        .createdBy(this.createdBy)
        .build();
  }
}
