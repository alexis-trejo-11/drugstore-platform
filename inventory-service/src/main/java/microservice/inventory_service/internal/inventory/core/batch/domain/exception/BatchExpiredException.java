package microservice.inventory_service.internal.inventory.core.batch.domain.exception;

import microservice.inventory_service.internal.inventory.core.inventory.domain.exception.base.InventoryException;

public class BatchExpiredException extends InventoryException {
    public BatchExpiredException(String message) {
        super(message);
    }
}
