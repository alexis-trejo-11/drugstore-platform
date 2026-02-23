package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for certification
 */
@Schema(description = "Certification Response DTO")
public record CertificationResponse(
    @Schema(description = "Certification type", example = "PHARMACIST_LICENSE") String type,

    @Schema(description = "License number", example = "PH-12345") String licenseNumber,

    @Schema(description = "Issuing authority", example = "State Board of Pharmacy") String issuingAuthority,

    @Schema(description = "Issue date", example = "2020-01-15") LocalDate issueDate,

    @Schema(description = "Expiration date", example = "2025-01-15") LocalDate expirationDate,

    @Schema(description = "Is certification still valid", example = "true") Boolean isValid,

    @Schema(description = "Is certification expired", example = "false") Boolean isExpired) {
}
