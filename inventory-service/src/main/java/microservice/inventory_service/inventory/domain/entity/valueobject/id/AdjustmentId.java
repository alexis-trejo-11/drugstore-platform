package microservice.inventory_service.inventory.domain.entity.valueobject.id;

import java.util.UUID;

public record AdjustmentId(String value) {
    public AdjustmentId {
        if (value == null) value = "";
        value = value.trim();
    }

    public static AdjustmentId of(String value) {
        return new AdjustmentId(value);
    }
    public static AdjustmentId of(UUID uuid) {
        return new AdjustmentId(uuid.toString());
    }
    public static AdjustmentId generate() {
        return new AdjustmentId(UUID.randomUUID().toString());
    }

}

