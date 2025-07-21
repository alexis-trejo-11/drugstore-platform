package microservice.ecommerce_cart_service.app.domain.excpetions;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(String message) {
        super(message);
    }
}
