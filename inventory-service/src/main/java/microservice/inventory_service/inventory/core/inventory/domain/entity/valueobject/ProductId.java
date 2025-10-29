package microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject;

import java.util.UUID;

public record ProductId(String value) {
    public ProductId {
        if (value == null) value = "";
        value = value.trim();
    }

    public static ProductId of(String value) {
        return new ProductId(value);
    }

    public static ProductId of(UUID uuid) {
        return new ProductId(uuid.toString());
    }
}
