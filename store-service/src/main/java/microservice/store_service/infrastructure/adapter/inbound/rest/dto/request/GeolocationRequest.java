package microservice.store_service.infrastructure.adapter.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import microservice.store_service.application.dto.command.valueobject.GeolocationCommand;

public record GeolocationRequest(
        @NotNull
        @Schema(description = "Latitude of the store location", example = "40.7128")
        Double latitude,

        @NotNull
        @Schema(description = "Longitude of the store location", example = "-74.0060")
        Double longitude
) {
    public GeolocationCommand toCommand() {
        return new GeolocationCommand(this.latitude, this.longitude);
    }
}
