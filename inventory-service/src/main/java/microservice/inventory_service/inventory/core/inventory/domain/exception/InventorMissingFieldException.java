package microservice.inventory_service.inventory.core.inventory.domain.exception;

public class InventorMissingFieldException extends InventoryValidationException {
    public InventorMissingFieldException(String message) {
        super(message);
    }
}
