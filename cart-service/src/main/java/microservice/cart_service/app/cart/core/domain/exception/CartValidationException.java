package microservice.cart_service.app.cart.core.domain.exception;

/**
 * Exception thrown when cart business validation fails.
 */
public class CartValidationException extends RuntimeException {

  public CartValidationException(String message) {
    super(message);
  }

  public CartValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
