package microservice.inventory_service.internal.purachse_order.domain.exception;

import microservice.inventory_service.internal.inventory.core.inventory.domain.exception.base.InventoryException;

public class OrderItemValidationException extends InventoryException {
    public OrderItemValidationException(String message) {
        super(message, "INVALID_ORDER_ITEM");
    }
}
