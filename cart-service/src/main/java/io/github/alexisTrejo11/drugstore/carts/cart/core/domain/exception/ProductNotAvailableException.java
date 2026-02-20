package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;

/**
 * Exception thrown when a product is not available for cart operations.
 */
public class ProductNotAvailableException extends RuntimeException {

  private final ProductId productId;

  public ProductNotAvailableException(String message) {
    super(message);
    this.productId = null;
  }

  public ProductNotAvailableException(ProductId productId) {
    super("Product with ID " + productId.value() + " is not available");
    this.productId = productId;
  }

  public ProductNotAvailableException(ProductId productId, String reason) {
    super("Product with ID " + productId.value() + " is not available: " + reason);
    this.productId = productId;
  }

  public ProductId getProductId() {
    return productId;
  }
}
