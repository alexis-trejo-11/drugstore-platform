package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.Address;

/**
 * Request DTO for employee address
 */
@Schema(description = "Address Request DTO")
public record AddressRequest(
    @NotNull @NotBlank @Schema(description = "Street address", example = "123 Main St", required = true) String street,

    @NotNull @NotBlank @Schema(description = "City", example = "New York", required = true) String city,

    @NotNull @NotBlank @Schema(description = "State/Province", example = "NY", required = true) String state,

    @NotNull @NotBlank @Schema(description = "Postal code", example = "10001", required = true) String postalCode,

    @NotNull @NotBlank @Schema(description = "Country", example = "USA", required = true) String country) {

  public Address toDomain() {
    return Address.of(
        this.street,
        this.city,
        this.state,
        this.postalCode,
        this.country);
  }
}
