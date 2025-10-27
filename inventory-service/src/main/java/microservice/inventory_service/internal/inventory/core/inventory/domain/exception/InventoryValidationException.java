package microservice.inventory_service.internal.inventory.core.inventory.domain.exception;

import microservice.inventory_service.internal.inventory.core.inventory.domain.exception.base.InventoryException;

public class InventoryValidationException extends InventoryException {
    public InventoryValidationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public InventoryValidationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

    public InventoryValidationException(String message) {
        super(message);
    }
}
