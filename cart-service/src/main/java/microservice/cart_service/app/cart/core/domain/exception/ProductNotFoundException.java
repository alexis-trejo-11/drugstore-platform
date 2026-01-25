package microservice.cart_service.app.cart.core.domain.exception;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;

/**
 * Exception thrown when a product is not found.
 */
public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException(String message) {
    super(message);
  }

  public ProductNotFoundException(ProductId productId) {
    super("Product with ID " + productId.value() + " not found");
  }
}
