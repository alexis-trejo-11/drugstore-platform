package org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject.GeolocationCommand;

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
