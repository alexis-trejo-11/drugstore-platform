package microservice.ecommerce_cart_service.app.domain.excpetions;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) {
        super(message);
    }

    public CartNotFoundException(Long cartId) {
        super("Cart not found with id: " + cartId);
    }
}

