package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception;

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
