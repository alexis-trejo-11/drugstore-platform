package microservice.inventory_service.internal.inventory.core.inventory.domain.exception;

import microservice.inventory_service.internal.inventory.core.inventory.domain.exception.base.InventoryException;

public class InventoryNotFoundException extends InventoryException {
    public InventoryNotFoundException(String message) {
        super(message, "INVENTORY_NOT_FOUND");
    }
}
