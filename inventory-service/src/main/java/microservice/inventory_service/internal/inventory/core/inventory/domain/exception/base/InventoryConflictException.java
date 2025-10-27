package microservice.inventory_service.internal.inventory.core.inventory.domain.exception.base;

public class InventoryConflictException extends InventoryException {
    protected InventoryConflictException(String message) {
        super(message, "INVENTORY_CONFLICT_ERROR");
    }


}
