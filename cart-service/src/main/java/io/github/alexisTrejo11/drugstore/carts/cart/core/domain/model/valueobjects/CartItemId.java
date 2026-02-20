package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValueObjectException;

/**
 * Value object representing a CartItem's unique identifier.
 */
public record CartItemId(String value) {

  public CartItemId {
    if (value == null) {
      throw new CartValueObjectException("CartItemId", "CartItem ID cannot be null");
    }
  }

  /**
   * Generates a new unique CartItemId.
   *
   * @return a new CartItemId with a generated UUID
   */
  public static CartItemId generate() {
    return new CartItemId(UUID.randomUUID().toString());
  }

  /**
   * Creates a CartItemId from a string representation.
   *
   * @param value the string representation of the UUID
   * @return a CartItemId with the parsed UUID
   * @throws CartValueObjectException if the string is not a valid UUID
   */
  public static CartItemId from(String value) {
    if (value == null || value.isBlank()) {
      throw new CartValueObjectException("CartItemId", "CartItem ID string cannot be null or blank");
    }
    try {
      return new CartItemId(value);
    } catch (IllegalArgumentException e) {
      throw new CartValueObjectException("CartItemId", "Invalid UUID format: " + value);
    }
  }

  /**
   * Creates a CartItemId from a UUID.
   *
   * @param value the UUID
   * @return a CartItemId with the given UUID
   */
  public static CartItemId from(UUID value) {
    return new CartItemId(value.toString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    CartItemId that = (CartItemId) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
