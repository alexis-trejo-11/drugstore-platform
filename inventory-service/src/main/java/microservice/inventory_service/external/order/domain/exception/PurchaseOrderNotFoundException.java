package microservice.inventory_service.external.order.domain.exception;

import microservice.inventory_service.internal.core.inventory.domain.exception.base.InventoryException;

public class PurchaseOrderNotFoundException extends InventoryException {
    public PurchaseOrderNotFoundException(String message) {
        super(message);
    }
}
