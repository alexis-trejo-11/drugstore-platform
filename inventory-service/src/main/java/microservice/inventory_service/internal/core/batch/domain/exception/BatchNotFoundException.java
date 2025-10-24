package microservice.inventory_service.internal.core.batch.domain.exception;

import microservice.inventory_service.internal.core.inventory.domain.exception.base.InventoryException;

public class BatchNotFoundException extends InventoryException {
    public BatchNotFoundException(String message) {
        super(message);
    }
}
