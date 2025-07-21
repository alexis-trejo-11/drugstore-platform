package microservice.ecommerce_cart_service.app.domain.excpetions;

public class ProductNotAvailableException extends RuntimeException {
    public ProductNotAvailableException(String message) {
        super(message);
    }

    public ProductNotAvailableException(Long productId) {
        super("Product not available with id: " + productId);
    }
}
