package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class InvalidStockAdjustmentException extends InventoryException {
    public InvalidStockAdjustmentException(String message) {
        super(message);
    }
}
