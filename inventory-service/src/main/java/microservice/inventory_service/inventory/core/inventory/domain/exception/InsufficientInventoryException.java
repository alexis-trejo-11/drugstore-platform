package microservice.inventory_service.inventory.core.inventory.domain.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class InsufficientInventoryException extends InventoryException {
    public InsufficientInventoryException(String message) {
        super(message);
    }
}
