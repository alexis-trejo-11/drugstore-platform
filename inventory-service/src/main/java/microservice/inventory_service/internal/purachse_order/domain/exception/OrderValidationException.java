package microservice.inventory_service.internal.purachse_order.domain.exception;

import microservice.inventory_service.internal.inventory.core.inventory.domain.exception.base.InventoryException;

public class OrderValidationException extends InventoryException {
    public OrderValidationException(String message) {
        super(message, "INVALID_ORDER");
    }
}
