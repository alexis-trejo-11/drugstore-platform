package microservice.cart_service.app.cart.core.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

import microservice.cart_service.app.cart.core.domain.exception.CartValueObjectException;

/**
 * Value object representing a Product's unique identifier.
 */
public record ProductId(String value) {

  public ProductId {
    if (value == null) {
      throw new CartValueObjectException("ProductId", "Product ID cannot be null");
    }
  }

  /**
   * Creates a ProductId from a string representation.
   *
   * @param value the string representation of the UUID
   * @return a ProductId with the parsed UUID
   * @throws CartValueObjectException if the string is not a valid UUID
   */
  public static ProductId from(String value) {
    if (value == null || value.isBlank()) {
      throw new CartValueObjectException("ProductId", "Product ID string cannot be null or blank");
    }
    try {
      return new ProductId(value);
    } catch (IllegalArgumentException e) {
      throw new CartValueObjectException("ProductId", "Invalid UUID format: " + value);
    }
  }

  /**
   * Creates a ProductId from a UUID.
   *
   * @param value the UUID
   * @return a ProductId with the given UUID
   */
  public static ProductId from(UUID value) {
    return new ProductId(value.toString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ProductId productId = (ProductId) o;
    return Objects.equals(value, productId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
