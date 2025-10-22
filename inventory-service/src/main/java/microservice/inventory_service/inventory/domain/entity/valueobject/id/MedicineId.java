package microservice.inventory_service.inventory.domain.entity.valueobject.id;

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
