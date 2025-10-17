package microservice.store_service.domain.model.valueobjects;

public record Geolocation(
    Double latitude,
    Double longitude
) {
    public void validate() {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees.");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees.");
        }
    }
}
