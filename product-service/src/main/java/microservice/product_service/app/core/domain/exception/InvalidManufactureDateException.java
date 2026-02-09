package microservice.product_service.app.core.domain.exception;

public class InvalidManufactureDateException extends ProductValidationException {
    public InvalidManufactureDateException(String message) {
        super(message);
    }
}
