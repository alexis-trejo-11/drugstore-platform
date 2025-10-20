package microservice.purchase.domain.exception;

import microservice.inventory.domain.exception.InventoryException;

public class PurchaseOrderNotFoundException extends InventoryException {
    public PurchaseOrderNotFoundException(String message) {
        super(message);
    }
}
