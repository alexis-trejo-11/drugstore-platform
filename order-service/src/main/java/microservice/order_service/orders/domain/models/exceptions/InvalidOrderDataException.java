package microservice.order_service.orders.domain.models.exceptions;

public class InvalidOrderDataException extends OrderDomainException {
    public InvalidOrderDataException(String message) {
        super(message, "invalid_order_data");
    }
}
