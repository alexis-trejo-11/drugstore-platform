package microservice.inventory_service.inventory.domain.exception;

public class InventoryException extends RuntimeException {
    public InventoryException(String message) {
        super(message);
    }
}
