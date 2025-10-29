package microservice.inventory_service.inventory.core.batch.domain.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class BatchNotFoundException extends InventoryException {
    public BatchNotFoundException(String message) {
        super(message);
    }
}
