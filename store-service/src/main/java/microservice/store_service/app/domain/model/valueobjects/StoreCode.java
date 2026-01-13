package microservice.store_service.app.domain.model.valueobjects;

public record StoreCode(String value) {
    public static StoreCode of(String value) {
        return new StoreCode(value);
    }

    public static StoreCode create(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Store code cannot be null or blank.");
        }

        if (value.length() != 6) {
            throw new IllegalArgumentException("Store code must be exactly 6 characters long.");
        }

        return new StoreCode(value);
    }

    public static StoreCode NONE = new StoreCode("");

}
