package microservice.order_service.orders.domain.models.exceptions;

public class InvalidOrderStateException extends OrderDomainException{
    public InvalidOrderStateException(String message) {
        super(message, "invalid_order_state");
    }
}
