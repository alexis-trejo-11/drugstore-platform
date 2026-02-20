package io.github.alexisTrejo11.drugstore.inventories.shared.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class BlankFieldException extends InventoryException {
    public BlankFieldException(String className, String fieldName) {
        super("Blank field: " + fieldName + " in class: " + className, "BLANK_FIELD_ERROR");
    }
}
