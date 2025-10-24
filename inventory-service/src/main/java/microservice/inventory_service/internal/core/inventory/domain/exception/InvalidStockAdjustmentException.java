package microservice.inventory_service.internal.core.inventory.domain.exception;

import microservice.inventory_service.internal.core.inventory.domain.exception.base.InventoryException;

public class InvalidStockAdjustmentException extends InventoryException {
    public InvalidStockAdjustmentException(String message) {
        super(message);
    }
}
