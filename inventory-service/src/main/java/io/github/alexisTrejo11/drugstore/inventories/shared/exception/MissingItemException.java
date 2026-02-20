package io.github.alexisTrejo11.drugstore.inventories.shared.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class MissingItemException extends InventoryException {
    public MissingItemException(String className, String itemName) {
        super("Missing item: " + itemName + " in class: " + className, "MISSING_ITEM_ERROR");
    }
}
