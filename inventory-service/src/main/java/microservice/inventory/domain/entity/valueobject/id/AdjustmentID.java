package microservice.inventory.domain.entity.valueobject.id;

import java.util.UUID;

public record AdjustmentID(String value) {
    public AdjustmentID {
        if (value == null) value = "";
        value = value.trim();
    }

    public static AdjustmentID of(String value) {
        return new AdjustmentID(value);
    }
    public static AdjustmentID of(UUID uuid) {
        return new AdjustmentID(uuid.toString());
    }
    public static AdjustmentID generate() {
        return new AdjustmentID(UUID.randomUUID().toString());
    }

}

