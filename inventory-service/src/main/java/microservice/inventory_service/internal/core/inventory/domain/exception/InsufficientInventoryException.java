package microservice.inventory_service.internal.core.inventory.domain.exception;

import microservice.inventory_service.internal.core.inventory.domain.exception.base.InventoryException;

public class InsufficientInventoryException extends InventoryException {
    public InsufficientInventoryException(String message) {
        super(message);
    }
}
