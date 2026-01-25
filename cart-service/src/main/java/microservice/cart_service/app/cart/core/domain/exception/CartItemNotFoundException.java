package microservice.cart_service.app.cart.core.domain.exception;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartItemId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;

/**
 * Exception thrown when a cart item is not found.
 */
public class CartItemNotFoundException extends RuntimeException {

  public CartItemNotFoundException(String message) {
    super(message);
  }

  public CartItemNotFoundException(CartItemId cartItemId) {
    super("Cart item with ID " + cartItemId.value() + " not found");
  }

  public CartItemNotFoundException(ProductId productId) {
    super("Cart item with product ID " + productId.value() + " not found");
  }
}
