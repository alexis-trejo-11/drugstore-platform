package microservice.store_service.domain.model.valueobjects;

public record StoreCode(
        String value
) {
    public static StoreCode of(String value) {
        return new StoreCode(value);
    }
}
