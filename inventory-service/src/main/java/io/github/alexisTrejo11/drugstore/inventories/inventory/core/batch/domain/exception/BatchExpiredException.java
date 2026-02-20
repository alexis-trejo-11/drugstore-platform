package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class BatchExpiredException extends InventoryException {
    public BatchExpiredException(String message) {
        super(message);
    }
}
