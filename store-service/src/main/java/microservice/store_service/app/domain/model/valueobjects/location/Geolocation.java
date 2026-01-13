package microservice.store_service.app.domain.model.valueobjects.location;

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

    public static Geolocation NONE = new Geolocation(0.0, 0.0);

    public static Geolocation create(Double latitude, Double longitude) {
        Geolocation geolocation = new Geolocation(latitude, longitude);
        geolocation.validate();
        return geolocation;
    }

    public static Geolocation of(Double latitude, Double longitude) {
        return new Geolocation(latitude, longitude);
    }
}
