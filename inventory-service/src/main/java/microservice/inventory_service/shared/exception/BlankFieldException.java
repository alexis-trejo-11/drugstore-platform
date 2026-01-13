package microservice.inventory_service.shared.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class BlankFieldException extends InventoryException {
    public BlankFieldException(String className, String fieldName) {
        super("Blank field: " + fieldName + " in class: " + className, "BLANK_FIELD_ERROR");
    }
}
