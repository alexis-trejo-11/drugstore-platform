package org.github.alexisTrejo11.drugstore.stores.application.port.in.command;


import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject.AddressCommand;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject.GeolocationCommand;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;

public record UpdateStoreLocationCommand(
        StoreID id,
        GeolocationCommand geolocation,
        AddressCommand address
) {}
