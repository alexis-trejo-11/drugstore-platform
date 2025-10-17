package microservice.store_service.application.command;

import microservice.store_service.domain.model.valueobjects.Geolocation;

public record GeolocationCommand(
    double latitude,
    double longitude
) {
    public Geolocation toGeolocation() {
        return new Geolocation(this.latitude, this.longitude);
    }
}
