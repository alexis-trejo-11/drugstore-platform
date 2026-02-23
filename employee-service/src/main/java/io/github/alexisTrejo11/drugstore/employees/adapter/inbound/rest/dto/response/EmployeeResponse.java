package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Response DTO for employee
 */
@Builder
@Schema(description = "Employee Response DTO")
public record EmployeeResponse(
    @Schema(description = "Employee unique identifier", example = "c1a2b3d4-e5f6-7890") String id,

    @Schema(description = "Employee number", example = "EMP-001") String employeeNumber,

    @Schema(description = "First name", example = "John") String firstName,

    @Schema(description = "Last name", example = "Doe") String lastName,

    @Schema(description = "Date of birth", example = "1990-05-15") LocalDate dateOfBirth,

    @Schema(description = "Employee address") AddressResponse address,

    @Schema(description = "Contact information") ContactInfoResponse contactInfo,

    @Schema(description = "Employee role", example = "PHARMACIST") String role,

    @Schema(description = "Employment type", example = "FULL_TIME") String employeeType,

    @Schema(description = "Employee status", example = "ACTIVE") String status,

    @Schema(description = "Department", example = "Pharmacy") String department,

    @Schema(description = "Store identifier", example = "store-123") String storeId,

    @Schema(description = "Hire date", example = "2024-01-15") LocalDate hireDate,

    @Schema(description = "Termination date", example = "2025-12-31") LocalDate terminationDate,

    @Schema(description = "List of certifications") List<CertificationResponse> certifications,

    @Schema(description = "Hourly rate", example = "25.50") BigDecimal hourlyRate,

    @Schema(description = "Weekly hours", example = "40") Integer weeklyHours,

    @Schema(description = "Can employee currently work", example = "true") Boolean canWork,

    @Schema(description = "Years of service", example = "3") Integer yearsOfService,

    @Schema(description = "Creation timestamp") LocalDateTime createdAt,

    @Schema(description = "Last update timestamp") LocalDateTime updatedAt,

    @Schema(description = "Created by", example = "admin@drugstore.com") String createdBy,

    @Schema(description = "Last modified by", example = "hr@drugstore.com") String lastModifiedBy) {
}
