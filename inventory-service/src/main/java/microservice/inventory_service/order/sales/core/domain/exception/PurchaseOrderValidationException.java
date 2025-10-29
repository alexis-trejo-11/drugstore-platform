package microservice.inventory_service.order.sales.core.domain.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class PurchaseOrderValidationException extends InventoryException {
    public PurchaseOrderValidationException(String message) {
        super(message, "PURCHASE_ORDER_VALIDATION_ERROR");
    }
}
