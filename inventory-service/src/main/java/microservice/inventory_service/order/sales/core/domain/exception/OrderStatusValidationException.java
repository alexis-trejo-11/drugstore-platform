package microservice.inventory_service.order.sales.core.domain.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class OrderStatusValidationException extends InventoryException {
    public OrderStatusValidationException(String message) {
        super(message, "PURCHASE_ORDER_STATUS_VALIDATION_ERROR");
    }
}
