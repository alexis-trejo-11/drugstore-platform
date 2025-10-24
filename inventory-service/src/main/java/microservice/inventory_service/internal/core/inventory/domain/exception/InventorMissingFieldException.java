package microservice.inventory_service.internal.core.inventory.domain.exception;

public class InventorMissingFieldException extends InventoryValidationException {
    public InventorMissingFieldException(String message) {
        super(message);
    }
}
