package microservice.inventory_service.order.supplier_purchase.domain.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class PurchaseOrderStatusValidationException extends InventoryException {
    public PurchaseOrderStatusValidationException(String message) {
        super(message, "PURCHASE_ORDER_VALIDATION_ERROR");
    }
}
