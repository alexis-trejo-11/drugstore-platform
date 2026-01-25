package microservice.cart_service.app.cart.core.domain.exception;

/**
 * Exception thrown when an invalid quantity is provided.
 */
public class InvalidQuantityException extends RuntimeException {

  private final int invalidQuantity;

  public InvalidQuantityException(String message) {
    super(message);
    this.invalidQuantity = 0;
  }

  public InvalidQuantityException(int quantity, String message) {
    super(message);
    this.invalidQuantity = quantity;
  }

  public InvalidQuantityException(int quantity) {
    super(String.format("Invalid quantity: %d. Quantity must be positive and not exceed maximum limit", quantity));
    this.invalidQuantity = quantity;
  }

  public int getInvalidQuantity() {
    return invalidQuantity;
  }
}
