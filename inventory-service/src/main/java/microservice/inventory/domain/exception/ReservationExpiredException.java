package microservice.inventory.domain.exception;

public class ReservationExpiredException extends InventoryException {
    public ReservationExpiredException(String message) {
        super(message);
    }
}
