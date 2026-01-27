package microservice.cart_service.app.cart.core.domain.exception;

/**
 * Exception thrown when there is a conflict in cart operations.
 */
public class CartConflictException extends RuntimeException {

  public CartConflictException(String message) {
    super(message);
  }

  public CartConflictException(String message, Throwable cause) {
    super(message, cause);
  }
}
