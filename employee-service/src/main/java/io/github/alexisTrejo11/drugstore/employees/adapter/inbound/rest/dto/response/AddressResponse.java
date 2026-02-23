package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for address
 */
@Schema(description = "Address Response DTO")
public record AddressResponse(
    @Schema(description = "Street address", example = "123 Main St") String street,

    @Schema(description = "City", example = "New York") String city,

    @Schema(description = "State/Province", example = "NY") String state,

    @Schema(description = "Postal code", example = "10001") String postalCode,

    @Schema(description = "Country", example = "USA") String country) {
}
