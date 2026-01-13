package microservice.store_service.app.domain.model.valueobjects;

import java.util.UUID;

public record StoreID(String value) {
    public static StoreID of(String value) {
        return new StoreID(value);
    }

    public static StoreID of(UUID uuid) {
        return new StoreID(uuid.toString());
    }

    public static StoreID generate() {
        return new StoreID(java.util.UUID.randomUUID().toString());
    }
}
