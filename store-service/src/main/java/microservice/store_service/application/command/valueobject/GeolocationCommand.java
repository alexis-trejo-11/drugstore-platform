package microservice.store_service.application.command.valueobject;

import microservice.store_service.domain.model.valueobjects.location.Geolocation;

public record GeolocationCommand(
    double latitude,
    double longitude
) {
    public Geolocation toGeolocation() {
        return new Geolocation(this.latitude, this.longitude);
    }
}
