package microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject;

import java.util.UUID;

public record UserId(String value) {
    public UserId {
        if (value == null) value = "";
        value = value.trim();
    }

    public static UserId of(String value) {
        return new UserId(value);
    }
    public static UserId of(UUID uuid) {
        return new UserId(uuid.toString());
    }
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }
}
