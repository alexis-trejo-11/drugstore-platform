package microservice.product_service.app.domain.exception;

public class InvalidManufactureDateException extends ProductValidationException {
    public InvalidManufactureDateException(String message) {
        super(message);
    }
}
