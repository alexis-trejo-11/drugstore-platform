package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValueObjectException;

/**
 * Value object representing a Customer's unique identifier.
 */
public record CustomerId(String value) {

  public CustomerId {
    if (value == null) {
      throw new CartValueObjectException("CustomerId", "Customer ID cannot be null");
    }
  }

  /**
   * Creates a CustomerId from a string representation.
   *
   * @param value the string representation of CustomerId
   * @return a CustomerId with the string
   */
  public static CustomerId from(String value) {
    if (value == null || value.isBlank()) {
      throw new CartValueObjectException("CustomerId", "Customer ID string cannot be null or blank");
    }
    return new CustomerId(value);
  }

  /**
   * Creates a CustomerId from a UUID.
   *
   * @param value the UUID
   * @return a CustomerId with the given UUID
   */
  public static CustomerId from(UUID value) {
    return new CustomerId(value.toString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    CustomerId that = (CustomerId) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
