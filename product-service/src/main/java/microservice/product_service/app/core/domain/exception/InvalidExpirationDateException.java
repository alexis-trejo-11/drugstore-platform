package microservice.product_service.app.core.domain.exception;

public class InvalidExpirationDateException extends ProductValidationException {
    public InvalidExpirationDateException(String message) {
        super(message);
    }
}
