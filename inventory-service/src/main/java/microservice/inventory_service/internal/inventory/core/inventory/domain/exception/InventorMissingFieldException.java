package microservice.inventory_service.internal.inventory.core.inventory.domain.exception;

public class InventorMissingFieldException extends InventoryValidationException {
    public InventorMissingFieldException(String message) {
        super(message);
    }
}
