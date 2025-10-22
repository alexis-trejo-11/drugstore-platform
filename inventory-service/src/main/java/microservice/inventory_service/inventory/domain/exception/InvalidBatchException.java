package microservice.inventory_service.inventory.domain.exception;

public class InvalidBatchException extends InventoryException {
    public InvalidBatchException(String message) {
        super(message);
    }
}
