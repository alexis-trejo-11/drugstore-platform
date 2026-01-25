package microservice.cart_service.app.cart.core.domain.model.valueobjects;

import java.util.Objects;

import microservice.cart_service.app.cart.core.domain.exception.CartValueObjectException;

/**
 * Value object representing product information cached in the cart.
 * This is a snapshot of product data at the time of adding to cart.
 */
public record ProductSnapshot(
    ProductId productId,
    String name,
    ItemPrice unitPrice,
    boolean isAvailable) {

  public ProductSnapshot {
    if (productId == null) {
      throw new CartValueObjectException("ProductSnapshot", "Product ID cannot be null");
    }
    if (name == null || name.isBlank()) {
      throw new CartValueObjectException("ProductSnapshot", "Product name cannot be null or blank");
    }
    if (unitPrice == null) {
      throw new CartValueObjectException("ProductSnapshot", "Unit price cannot be null");
    }
  }

  /**
   * Creates a ProductSnapshot with all required fields.
   *
   * @param productId   the product's unique identifier
   * @param name        the product name
   * @param unitPrice   the unit price of the product
   * @param isAvailable whether the product is available
   * @return a new ProductSnapshot
   */
  public static ProductSnapshot create(ProductId productId, String name, ItemPrice unitPrice, boolean isAvailable) {
    return new ProductSnapshot(productId, name, unitPrice, isAvailable);
  }

  /**
   * Creates a ProductSnapshot from string/primitive values.
   *
   * @param productIdStr the product ID as string
   * @param name         the product name
   * @param price        the price as BigDecimal
   * @param isAvailable  whether the product is available
   * @return a new ProductSnapshot
   */
  public static ProductSnapshot from(String productIdStr, String name, java.math.BigDecimal price,
      boolean isAvailable) {
    return new ProductSnapshot(
        ProductId.from(productIdStr),
        name,
        ItemPrice.create(price),
        isAvailable);
  }

  /**
   * Creates an unavailable product snapshot.
   *
   * @param productId the product ID
   * @param name      the product name
   * @return a ProductSnapshot marked as unavailable
   */
  public static ProductSnapshot unavailable(ProductId productId, String name) {
    return new ProductSnapshot(productId, name, ItemPrice.zero(), false);
  }

  /**
   * Returns a copy with updated availability status.
   *
   * @param available the new availability status
   * @return a new ProductSnapshot with updated availability
   */
  public ProductSnapshot withAvailability(boolean available) {
    return new ProductSnapshot(this.productId, this.name, this.unitPrice, available);
  }

  /**
   * Returns a copy with updated price.
   *
   * @param newPrice the new price
   * @return a new ProductSnapshot with updated price
   */
  public ProductSnapshot withPrice(ItemPrice newPrice) {
    return new ProductSnapshot(this.productId, this.name, newPrice, this.isAvailable);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ProductSnapshot that = (ProductSnapshot) o;
    return Objects.equals(productId, that.productId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productId);
  }

  @Override
  public String toString() {
    return String.format("ProductSnapshot{productId=%s, name='%s', unitPrice=%s, available=%s}",
        productId, name, unitPrice, isAvailable);
  }
}
