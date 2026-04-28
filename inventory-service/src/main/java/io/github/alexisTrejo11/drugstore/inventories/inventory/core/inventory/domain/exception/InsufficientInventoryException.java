package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class InsufficientInventoryException extends InventoryException {
    public InsufficientInventoryException(String message) {
        super(message);
    }
}
