package org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.UpdateStoreLocationCommand;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;

@Schema(description = "Location information including geolocation and address")
public record StoreLocationRequest(
        @Schema(description = "Geolocation information of the store")
        @NotNull
        GeolocationRequest geolocation,

        @Schema(description = "Address information of the store")
        @NotNull
        AddressRequest address)
{
    public UpdateStoreLocationCommand toCommand(StoreID storeID)  {
        return new UpdateStoreLocationCommand(
                storeID,
                this.geolocation.toCommand(),
                this.address.toCommand()
        );
    }
}
