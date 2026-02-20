package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class InvalidBatchFieldException extends InventoryException {
    public InvalidBatchFieldException(String message) {
        super(message, "INVALID_BATCH_FIELD");
    }
}
