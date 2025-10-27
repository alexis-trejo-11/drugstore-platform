package microservice.inventory_service.internal.inventory.core.inventory.domain.exception;

import microservice.inventory_service.internal.inventory.core.inventory.domain.exception.base.InventoryException;

public class NegativeQuantityException extends InventoryException {
    public NegativeQuantityException(String entityName, String fieldName, int quantity) {
        super(
            String.format("Negative quantity for %s in field %s: %d", entityName, fieldName, quantity)
        );
    }
}
