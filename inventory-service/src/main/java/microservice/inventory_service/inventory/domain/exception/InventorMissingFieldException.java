package microservice.inventory_service.inventory.domain.exception;

public class InventorMissingFieldException extends RuntimeException {
    public InventorMissingFieldException(String message) {
        super(message);
    }
}
