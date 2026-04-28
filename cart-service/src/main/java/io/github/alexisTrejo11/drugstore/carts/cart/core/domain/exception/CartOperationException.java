package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception;

/**
 * Exception thrown when a cart operation fails.
 */
public class CartOperationException extends RuntimeException {

  private final String operation;

  public CartOperationException(String message) {
    super(message);
    this.operation = null;
  }

  public CartOperationException(String operation, String message) {
    super(message);
    this.operation = operation;
  }

  public CartOperationException(String operation, String message, Throwable cause) {
    super(message, cause);
    this.operation = operation;
  }

  public String getOperation() {
    return operation;
  }

  @Override
  public String toString() {
    if (operation == null)
      return super.toString();
    return String.format("Operation [%s] failed: %s", operation, getMessage());
  }
}
