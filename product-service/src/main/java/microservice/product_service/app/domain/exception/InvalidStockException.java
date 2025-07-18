package microservice.product_service.app.domain.exception;

public class InvalidStockException extends ProductValidationException {
    public InvalidStockException(String message) {
        super(message);
    }
}
