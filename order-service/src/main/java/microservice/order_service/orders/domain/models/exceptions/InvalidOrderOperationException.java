package microservice.order_service.orders.domain.models.exceptions;

public class InvalidOrderOperationException extends RuntimeException {
    public InvalidOrderOperationException(String message) {
        super(message);
    }
}
