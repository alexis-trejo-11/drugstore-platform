package microservice.inventory_service.internal.core.movement.domain.valueobject;

import java.util.UUID;

public record MovementId(String value) {
    public MovementId {
        if (value == null) value = "";
        value = value.trim();
    }

    public static MovementId of(String value) {
        return new MovementId(value);
    }

    public static MovementId of(UUID uuid) {
        return new MovementId(uuid.toString());
    }

    public static MovementId generate() {
        return new MovementId(UUID.randomUUID().toString());
    }

}
