package microservice.inventory_service.shared.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class MissingFieldException extends InventoryException {
    public MissingFieldException(String className, String fieldName) {
        super("Missing field: " + fieldName + " in class: " + className, "MISSING_FIELD_ERROR");
    }
}

