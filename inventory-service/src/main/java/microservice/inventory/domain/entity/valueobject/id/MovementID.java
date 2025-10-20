package microservice.inventory.domain.entity.valueobject.id;

import java.util.UUID;

public record MovementID(String value) {
    public MovementID {
        if (value == null) value = "";
        value = value.trim();
    }

    public static MovementID of(String value) {
        return new MovementID(value);
    }

    public static MovementID of(UUID uuid) {
        return new MovementID(uuid.toString());
    }

    public static MovementID generate() {
        return new MovementID(UUID.randomUUID().toString());
    }

}
