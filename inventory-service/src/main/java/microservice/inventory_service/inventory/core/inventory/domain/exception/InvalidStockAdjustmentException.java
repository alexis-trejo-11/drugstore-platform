package microservice.inventory_service.inventory.core.inventory.domain.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class InvalidStockAdjustmentException extends InventoryException {
    public InvalidStockAdjustmentException(String message) {
        super(message);
    }
}
