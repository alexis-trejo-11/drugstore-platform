package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.AddCertificationCommand;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.Certification;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Request DTO for adding certification to employee
 */
@Schema(description = "Add Certification Request DTO")
public record AddCertificationRequest(
    @NotNull @Schema(description = "Certification type", example = "PHARMACIST_LICENSE", required = true) Certification.CertificationType type,

    @NotNull @NotBlank @Schema(description = "License number", example = "PH-12345", required = true) String licenseNumber,

    @NotNull @NotBlank @Schema(description = "Issuing authority", example = "State Board of Pharmacy", required = true) String issuingAuthority,

    @NotNull @Schema(description = "Issue date", example = "2020-01-15", required = true) LocalDate issueDate,

    @NotNull @Schema(description = "Expiration date", example = "2025-01-15", required = true) LocalDate expirationDate,

    @NotNull @NotBlank @Schema(description = "User adding the certification", example = "admin@drugstore.com", required = true) String addedBy) {

  public AddCertificationCommand toCommand(EmployeeId employeeId) {
    Certification certification = Certification.of(
        this.licenseNumber,
        this.issuingAuthority,
        this.issueDate,
        this.expirationDate,
        this.type);

    return new AddCertificationCommand(
        employeeId,
        certification,
        this.addedBy);
  }
}
