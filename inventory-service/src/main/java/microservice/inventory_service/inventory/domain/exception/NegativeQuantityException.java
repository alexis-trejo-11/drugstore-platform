package microservice.inventory_service.inventory.domain.exception;

public class NegativeQuantityException extends InventoryException {
    public NegativeQuantityException(String entityName, String fieldName, int quantity) {
        super(
            String.format("Negative quantity for %s in field %s: %d", entityName, fieldName, quantity)
        );
    }
}
