package microservice.inventory.domain.entity.valueobject.id;

import java.util.UUID;

public record UserID(String value) {
    public UserID {
        if (value == null) value = "";
        value = value.trim();
    }

    public static UserID of(String value) {
        return new UserID(value);
    }
    public static UserID of(UUID uuid) {
        return new UserID(uuid.toString());
    }
    public static UserID generate() {
        return new UserID(UUID.randomUUID().toString());
    }
}
