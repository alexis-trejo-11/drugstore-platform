package microservice.store_service.application.dto.command;


import jakarta.validation.constraints.NotNull;
import microservice.store_service.application.dto.command.valueobject.AddressCommand;
import microservice.store_service.application.dto.command.valueobject.GeolocationCommand;
import microservice.store_service.domain.model.valueobjects.StoreID;

public record UpdateStoreLocationCommand(
        @NotNull StoreID id,
        @NotNull GeolocationCommand geolocation,
        @NotNull AddressCommand address
) {}
