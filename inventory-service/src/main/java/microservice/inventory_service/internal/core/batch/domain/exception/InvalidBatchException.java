package microservice.inventory_service.internal.core.batch.domain.exception;

import microservice.inventory_service.internal.core.inventory.domain.exception.base.InventoryException;

public class InvalidBatchException extends InventoryException {
    public InvalidBatchException(String message) {
        super(message);
    }
}
