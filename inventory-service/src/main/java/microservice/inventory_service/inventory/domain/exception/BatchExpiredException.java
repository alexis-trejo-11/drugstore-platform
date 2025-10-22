package microservice.inventory_service.inventory.domain.exception;

public class BatchExpiredException extends InventoryException {
    public BatchExpiredException(String message) {
        super(message);
    }
}
