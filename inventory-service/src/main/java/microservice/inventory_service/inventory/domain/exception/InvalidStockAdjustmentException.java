package microservice.inventory_service.inventory.domain.exception;

public class InvalidStockAdjustmentException extends InventoryException {
    public InvalidStockAdjustmentException(String message) {
        super(message);
    }
}
