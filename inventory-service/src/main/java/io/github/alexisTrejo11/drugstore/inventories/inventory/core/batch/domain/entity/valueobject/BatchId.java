package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject;

import java.util.UUID;

public record BatchId(String value) {
    public BatchId {
        if (value == null) value = "";
        value = value.trim();
    }

    public static BatchId of(String value) {
        return new BatchId(value);
    }

    public static BatchId of(UUID uuid) {
        return new BatchId(uuid.toString());
    }

    public static BatchId generate() {
        return new BatchId(UUID.randomUUID().toString());
    }

}
