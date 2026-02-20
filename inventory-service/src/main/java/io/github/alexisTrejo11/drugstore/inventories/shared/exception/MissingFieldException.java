package io.github.alexisTrejo11.drugstore.inventories.shared.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class MissingFieldException extends InventoryException {
    public MissingFieldException(String className, String fieldName) {
        super("Missing field: " + fieldName + " in class: " + className, "MISSING_FIELD_ERROR");
    }
}

