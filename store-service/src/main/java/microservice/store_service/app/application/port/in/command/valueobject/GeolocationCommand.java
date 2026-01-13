package microservice.store_service.app.application.port.in.command.valueobject;

import microservice.store_service.app.domain.model.valueobjects.location.Geolocation;

public record GeolocationCommand(
    double latitude,
    double longitude
) {
    public Geolocation toGeolocation() {
        return new Geolocation(this.latitude, this.longitude);
    }
}
