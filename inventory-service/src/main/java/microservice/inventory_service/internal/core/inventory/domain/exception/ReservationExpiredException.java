package microservice.inventory_service.internal.core.inventory.domain.exception;

import microservice.inventory_service.internal.core.inventory.domain.exception.base.InventoryException;

public class ReservationExpiredException extends InventoryException {
    public ReservationExpiredException(String message) {
        super(message);
    }
}
