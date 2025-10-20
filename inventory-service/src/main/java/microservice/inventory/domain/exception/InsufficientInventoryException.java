package microservice.inventory.domain.exception;

public class InsufficientInventoryException extends InventoryException {
    public InsufficientInventoryException(String message) {
        super(message);
    }
}
