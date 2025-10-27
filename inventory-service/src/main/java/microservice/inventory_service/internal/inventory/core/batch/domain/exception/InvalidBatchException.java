package microservice.inventory_service.internal.inventory.core.batch.domain.exception;

import microservice.inventory_service.internal.inventory.core.inventory.domain.exception.base.InventoryException;

public class InvalidBatchException extends InventoryException {
    public InvalidBatchException(String message) {
        super(message);
    }
}
