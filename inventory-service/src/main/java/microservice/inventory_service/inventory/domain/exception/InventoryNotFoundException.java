package microservice.inventory_service.inventory.domain.exception;

public class InventoryNotFoundException extends InventoryException {
    public InventoryNotFoundException(String message) {
        super(message);
    }
}
