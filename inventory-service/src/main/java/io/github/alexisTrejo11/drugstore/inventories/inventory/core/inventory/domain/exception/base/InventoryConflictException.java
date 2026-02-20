package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base;

public class InventoryConflictException extends InventoryException {
    protected InventoryConflictException(String message) {
        super(message, "INVENTORY_CONFLICT_ERROR");
    }


}
