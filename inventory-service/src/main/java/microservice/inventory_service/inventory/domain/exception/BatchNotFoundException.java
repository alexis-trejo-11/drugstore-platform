package microservice.inventory_service.inventory.domain.exception;

public class BatchNotFoundException extends InventoryException {
    public BatchNotFoundException(String message) {
        super(message);
    }
}
