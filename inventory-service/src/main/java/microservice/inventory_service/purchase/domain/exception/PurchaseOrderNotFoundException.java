package microservice.inventory_service.purchase.domain.exception;

import microservice.inventory_service.inventory.domain.exception.InventoryException;

public class PurchaseOrderNotFoundException extends InventoryException {
    public PurchaseOrderNotFoundException(String message) {
        super(message);
    }
}
