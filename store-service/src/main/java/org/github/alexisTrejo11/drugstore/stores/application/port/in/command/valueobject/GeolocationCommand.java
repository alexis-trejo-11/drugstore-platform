package org.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject;

import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.location.Geolocation;

public record GeolocationCommand(
    double latitude,
    double longitude
) {
    public Geolocation toGeolocation() {
        return new Geolocation(this.latitude, this.longitude);
    }
}
