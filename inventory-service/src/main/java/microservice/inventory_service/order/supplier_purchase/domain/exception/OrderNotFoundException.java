package microservice.inventory_service.order.supplier_purchase.domain.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class OrderNotFoundException extends InventoryException {
    public OrderNotFoundException(String message) {
        super(message, "ORDER_NOT_FOUND");
    }
}
