package io.github.alexisTrejo11.drugstore.stores.application.port.in.command;


import io.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject.AddressCommand;
import io.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject.GeolocationCommand;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;

public record UpdateStoreLocationCommand(
        StoreID id,
        GeolocationCommand geolocation,
        AddressCommand address
) {}
