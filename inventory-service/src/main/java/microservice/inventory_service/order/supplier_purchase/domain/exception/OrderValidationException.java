package microservice.inventory_service.order.supplier_purchase.domain.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class OrderValidationException extends InventoryException {
    public OrderValidationException(String message) {
        super(message, "INVALID_ORDER");
    }
}
