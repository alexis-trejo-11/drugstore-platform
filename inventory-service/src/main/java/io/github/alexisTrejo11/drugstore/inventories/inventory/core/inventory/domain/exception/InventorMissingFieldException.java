package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception;

public class InventorMissingFieldException extends InventoryValidationException {
    public InventorMissingFieldException(String message) {
        super(message);
    }
}
