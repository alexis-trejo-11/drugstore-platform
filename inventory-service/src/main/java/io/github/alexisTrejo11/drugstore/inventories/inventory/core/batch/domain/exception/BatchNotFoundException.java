package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class BatchNotFoundException extends InventoryException {
    public BatchNotFoundException(String message) {
        super(message);
    }
}
