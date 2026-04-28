package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class InvalidBatchException extends InventoryException {
    public InvalidBatchException(String message) {
        super(message , "INVALID_BATCH_EXCEPTION");
    }
}
