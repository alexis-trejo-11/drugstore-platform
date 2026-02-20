package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base;

public class InventoryNotFoundException extends InventoryException {
    public InventoryNotFoundException(String message) {
        super(message, "INVENTORY_NOT_FOUND");
    }
}
