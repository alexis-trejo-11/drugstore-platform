package microservice.inventory.domain.exception;

public class InvalidBatchException extends InventoryException {
    public InvalidBatchException(String message) {
        super(message);
    }
}
