package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject;

import java.util.UUID;

public record ReservationId(String value) {
    public ReservationId {
        if (value == null) value = "";
        value = value.trim();
    }

    public static ReservationId of(String value) {
        return new ReservationId(value);
    }
    public static ReservationId of(UUID uuid) {
        return new ReservationId(uuid.toString());
    }
    public static ReservationId generate() {
        return new ReservationId(UUID.randomUUID().toString());
    }

}