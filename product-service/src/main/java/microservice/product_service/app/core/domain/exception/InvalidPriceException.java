package microservice.product_service.app.core.domain.exception;

public class InvalidPriceException extends ProductValidationException {
    public InvalidPriceException(String message) {
        super(message);
    }
}

