package microservice.store_service.domain.model.valueobjects;

public record StoreName(
        String value
) {

    public static StoreName of(String value) {
        return new StoreName(value);
    }
}
