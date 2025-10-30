package microservice.inventory_service.inventory.core.batch.domain.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class InvalidBatchFieldException extends InventoryException {
    public InvalidBatchFieldException(String message) {
        super(message, "INVALID_BATCH_FIELD");
    }
}
