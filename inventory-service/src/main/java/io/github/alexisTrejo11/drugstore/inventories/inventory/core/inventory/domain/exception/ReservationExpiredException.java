package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class ReservationExpiredException extends InventoryException {
    public ReservationExpiredException(String message) {
        super(message);
    }
}
