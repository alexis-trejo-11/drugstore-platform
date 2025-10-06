package microservice.order_service.orders.domain.models.exceptions;

public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(String message) {
        super(message);
    }
    
    public InvalidOrderStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
