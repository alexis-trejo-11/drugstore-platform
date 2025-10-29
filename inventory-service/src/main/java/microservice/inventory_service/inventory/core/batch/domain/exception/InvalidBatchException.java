package microservice.inventory_service.inventory.core.batch.domain.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class InvalidBatchException extends InventoryException {
    public InvalidBatchException(String message) {
        super(message);
    }
}
