package microservice.store_service.app.application.port.in.command;


import microservice.store_service.app.application.port.in.command.valueobject.AddressCommand;
import microservice.store_service.app.application.port.in.command.valueobject.GeolocationCommand;
import microservice.store_service.app.domain.model.valueobjects.StoreID;

public record UpdateStoreLocationCommand(
        StoreID id,
        GeolocationCommand geolocation,
        AddressCommand address
) {}
