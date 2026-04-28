package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception;

/**
 * Exception thrown when a Cart value object validation fails.
 * Contains information about which value object caused the error.
 */
public class CartValueObjectException extends RuntimeException {
  private final String valueObject;

  public CartValueObjectException(String message) {
    super(message);
    this.valueObject = null;
  }

  public CartValueObjectException(String valueObject, String message) {
    super(message);
    this.valueObject = valueObject;
  }

  public CartValueObjectException(String valueObject, String message, Throwable cause) {
    super(message, cause);
    this.valueObject = valueObject;
  }

  public String getValueObject() {
    return valueObject;
  }

  @Override
  public String toString() {
    if (valueObject == null)
      return super.toString();
    return String.format("%s: %s", valueObject, getMessage());
  }
}
