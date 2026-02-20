package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base;

public class InventoryValidationException extends InventoryException {
    protected InventoryValidationException(String message) {
        super(message, "INVENTORY_VALIDATION_ERROR");
    }


}
