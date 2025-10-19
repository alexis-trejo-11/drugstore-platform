package microservice.store_service.infrastructure.adapter.inbound.rest.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import microservice.store_service.application.dto.command.UpdateStoreLocationCommand;
import microservice.store_service.domain.model.valueobjects.StoreID;

@Schema(description = "Location information including geolocation and address")
public record StoreLocationRequest(
        @Schema(description = "Geolocation information of the store")
        GeolocationRequest geolocation,

        @Schema(description = "Address information of the store")
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
