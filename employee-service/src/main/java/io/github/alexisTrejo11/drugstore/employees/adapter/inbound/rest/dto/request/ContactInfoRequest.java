package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.ContactInfo;

/**
 * Request DTO for employee contact information
 */
@Schema(description = "Contact Info Request DTO")
public record ContactInfoRequest(
    @NotNull @NotBlank @Email @Schema(description = "Email address", example = "john.doe@drugstore.com", required = true) String email,

    @NotNull @NotBlank @Schema(description = "Phone number", example = "+1-555-0123", required = true) String phone,

    @Schema(description = "Emergency contact name", example = "Jane Doe") String emergencyContact,

    @Schema(description = "Emergency phone number", example = "+1-555-9999") String emergencyPhone) {

  public ContactInfo toDomain() {
    return ContactInfo.of(this.email, this.phone, this.emergencyContact, this.emergencyPhone);
  }
}
