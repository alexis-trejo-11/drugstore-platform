package microservice.cart_service.app.cart.core.domain.exception;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;

/**
 * Exception thrown when a cart is not found.
 */
public class CartNotFoundException extends RuntimeException {

  public CartNotFoundException(String message) {
    super(message);
  }

  public CartNotFoundException(CartId cartId) {
    super("Cart with ID " + cartId.value() + " not found");
  }

  public CartNotFoundException(CustomerId customerId) {
    super("Cart for customer " + customerId.value() + " not found");
  }
}
