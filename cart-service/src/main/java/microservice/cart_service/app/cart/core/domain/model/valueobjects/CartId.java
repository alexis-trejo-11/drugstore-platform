package microservice.cart_service.app.cart.core.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

import microservice.cart_service.app.cart.core.domain.exception.CartValueObjectException;

/**
 * Value object representing a Cart's unique identifier.
 */
public record CartId(String value) {

  public CartId {
    if (value == null) {
      throw new CartValueObjectException("CartId", "Cart ID cannot be null");
    }
  }

  /**
   * Generates a new unique CartId.
   *
   * @return a new CartId with a generated UUID
   */
  public static CartId generate() {
    return new CartId(UUID.randomUUID().toString());
  }

  /**
   * Creates a CartId from a string representation.
   *
   * @param value the string representation of the UUID
   * @return a CartId with the parsed UUID
   * @throws CartValueObjectException if the string is not a valid UUID
   */
  public static CartId from(String value) {
    if (value == null || value.isBlank()) {
      throw new CartValueObjectException("CartId", "Cart ID string cannot be null or blank");
    }
    try {
      return new CartId(value);
    } catch (IllegalArgumentException e) {
      throw new CartValueObjectException("CartId", "Invalid UUID format: " + value);
    }
  }

  /**
   * Creates a CartId from a UUID.
   *
   * @param value the UUID
   * @return a CartId with the given UUID
   */
  public static CartId from(UUID value) {
    return new CartId(value.toString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    CartId cartId = (CartId) o;
    return Objects.equals(value, cartId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
