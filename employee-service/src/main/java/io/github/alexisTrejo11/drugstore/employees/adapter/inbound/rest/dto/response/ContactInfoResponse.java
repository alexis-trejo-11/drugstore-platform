package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for contact information
 */
@Schema(description = "Contact Info Response DTO")
public record ContactInfoResponse(
    @Schema(description = "Email address", example = "john.doe@drugstore.com") String email,

    @Schema(description = "Phone number", example = "+1-555-0123") String phone,

    @Schema(description = "Emergency contact name", example = "Jane Doe") String emergencyContact,

    @Schema(description = "Emergency phone number", example = "+1-555-9999") String emergencyPhone) {
}
