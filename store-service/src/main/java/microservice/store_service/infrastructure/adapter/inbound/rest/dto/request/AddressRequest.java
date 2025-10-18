package microservice.store_service.infrastructure.adapter.inbound.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import microservice.store_service.application.command.valueobject.AddressCommand;

@Schema(description = "Address Request DTO")
public record AddressRequest(
        @NotBlank
        @Schema(description = "Country", example = "Peru")
        String country,

        @NotBlank
        @Schema(description = "State / Region", example = "Lima")
        String state,

        @NotBlank
        @Schema(description = "City", example = "Lima")
        String city,

        @Schema(description = "Postal / ZIP exactCode", example = "15001")
        String zipCode,

        @Schema(description = "Neighborhood / District", example = "Miraflores")
        String neighborhood,

        @NotBlank
        @Schema(description = "Street name", example = "Av. Larco")
        String street,

        @NotBlank
        @Schema(description = "Number / building", example = "123")
        String number
) {
    public AddressCommand toCommand() {
        return new AddressCommand(
                this.country,
                this.state,
                this.city,
                this.zipCode,
                this.neighborhood,
                this.street,
                this.number
        );
    }
}
