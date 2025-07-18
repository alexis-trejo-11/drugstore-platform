package microservice.product_service.app.domain.exception;

public class ExpiredProductException extends ProductValidationException {
    public ExpiredProductException(String message) {
        super(message);
    }
}
