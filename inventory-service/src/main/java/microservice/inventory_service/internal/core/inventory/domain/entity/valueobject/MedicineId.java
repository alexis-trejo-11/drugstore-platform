package microservice.inventory_service.internal.core.inventory.domain.entity.valueobject;

import java.util.UUID;

public record MedicineId(String value) {
    public MedicineId {
        if (value == null) value = "";
        value = value.trim();
    }

    public static MedicineId of(String value) {
        return new MedicineId(value);
    }

    public static MedicineId of(UUID uuid) {
        return new MedicineId(uuid.toString());
    }
}
