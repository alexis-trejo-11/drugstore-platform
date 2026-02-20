package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject;

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

