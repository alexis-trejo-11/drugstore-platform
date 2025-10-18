package microservice.store_service.application.command;


import jakarta.validation.constraints.NotNull;
import microservice.store_service.application.command.valueobject.AddressCommand;
import microservice.store_service.application.command.valueobject.GeolocationCommand;
import microservice.store_service.domain.model.valueobjects.StoreID;

public record UpdateStoreLocationCommand(
        @NotNull StoreID storeID,
        @NotNull GeolocationCommand geolocation,
        @NotNull AddressCommand address
) {}
