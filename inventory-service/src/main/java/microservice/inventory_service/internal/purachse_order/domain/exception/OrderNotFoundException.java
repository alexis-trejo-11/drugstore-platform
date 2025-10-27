package microservice.inventory_service.internal.purachse_order.domain.exception;

import microservice.inventory_service.internal.inventory.core.inventory.domain.exception.base.InventoryException;

public class OrderNotFoundException extends InventoryException {
    public OrderNotFoundException(String message) {
        super(message, "ORDER_NOT_FOUND");
    }
}
