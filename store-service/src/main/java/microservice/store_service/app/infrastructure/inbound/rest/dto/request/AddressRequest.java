package microservice.store_service.app.infrastructure.inbound.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import microservice.store_service.app.application.port.in.command.valueobject.AddressCommand;

@Schema(description = "Address Request DTO")
public record AddressRequest(
        @NotBlank @NotNull
        @Schema(description = "Country", example = "Peru")
        String country,

        @NotBlank @NotNull
        @Schema(description = "State / Region", example = "Lima")
        String state,

        @NotBlank @NotNull
        @Schema(description = "City", example = "Lima")
        String city,

        @Schema(description = "Postal / ZIP exactCode", example = "15001")
        String zipCode,

        @NotNull
        @Schema(description = "Neighborhood / District", example = "Miraflores")
        String neighborhood,

        @NotBlank @NotNull
        @Schema(description = "Street name", example = "Av. Larco")
        String street,

        @NotBlank @NotNull
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
