package microservice.store_service.infrastructure.adapter.inbound.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Address Response DTO")
public record AddressResponse(
    @Schema(description = "Country", example = "Peru")
    String country,
    @Schema(description = "State / Region", example = "Lima")
    String state,
    @Schema(description = "City", example = "Lima")
    String city,
    @Schema(description = "Postal / ZIP exactCode", example = "15001")
    String zipCode,
    @Schema(description = "Neighborhood / District", example = "Miraflores")
    String neighborhood,
    @Schema(description = "Street name", example = "Av. Larco")
    String street,
    @Schema(description = "Number / building", example = "123")
    String number,
    @Schema(description = "Full formatted address", example = "Av. Larco 123, Miraflores, Lima, Peru")
    String fullAddress
) {}
