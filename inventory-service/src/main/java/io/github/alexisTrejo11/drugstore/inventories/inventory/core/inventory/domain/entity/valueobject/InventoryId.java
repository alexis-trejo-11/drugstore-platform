package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject;

import java.util.UUID;

public record InventoryId(String value) {
    public InventoryId {
        if (value == null) value = "";
        value = value.trim();
    }

    public static InventoryId of(String value) {
        return new InventoryId(value);
    }

    public static InventoryId of(UUID uuid) {
        return new InventoryId(uuid.toString());
    }

    public static InventoryId generate() {
        return new InventoryId(UUID.randomUUID().toString());
    }

}
