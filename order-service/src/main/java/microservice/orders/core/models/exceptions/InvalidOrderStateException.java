package microservice.orders.core.models.exceptions;

public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(String message) {
        super(message);
    }
    
    public InvalidOrderStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
