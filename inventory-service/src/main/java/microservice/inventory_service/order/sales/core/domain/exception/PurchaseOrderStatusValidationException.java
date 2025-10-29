package microservice.inventory_service.order.sales.core.domain.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;
import microservice.inventory_service.shared.domain.order.OrderStatus;

public class PurchaseOrderStatusValidationException extends InventoryException {
    public PurchaseOrderStatusValidationException(OrderStatus expectedStatus, OrderStatus actualStatus, String operation) {
        super(String.format("PurchaseOrder status with %s must be %s to %s", actualStatus, expectedStatus, operation), "INVALID_PURCHASE_ORDER_STATUS");
    }
}
