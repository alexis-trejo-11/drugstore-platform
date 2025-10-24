package microservice.inventory_service.internal.core.batch.domain.exception;

import microservice.inventory_service.internal.core.inventory.domain.exception.base.InventoryException;

public class BatchExpiredException extends InventoryException {
    public BatchExpiredException(String message) {
        super(message);
    }
}
