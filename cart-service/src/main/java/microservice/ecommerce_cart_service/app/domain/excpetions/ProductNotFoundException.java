package microservice.ecommerce_cart_service.app.domain.excpetions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(Long productId) {
        super("Product not found with id: " + productId);
    }
}
