package microservice.inventory_service.internal.core.alert.domain.entity.valueobject;

import java.util.UUID;

public record AlertId(String value) {
    public AlertId {
        if (value == null) value = "";
        value = value.trim();
    }

    public static AlertId of(String value) {
        return new AlertId(value);
    }

    public static AlertId of(UUID uuid) {
        return new AlertId(uuid.toString());
    }

    public static AlertId generate() {
        return new AlertId(UUID.randomUUID().toString());
    }

}
