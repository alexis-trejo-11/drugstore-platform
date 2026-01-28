package microservice.cart_service.app.product.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;

/**
 * Domain model representing a Product in the catalog.
 *
 * This entity holds all necessary information about a product.
 * Does not contain business logic act more like a data class.
 *
 * This class is replication denormalized from the Product class on the
 * product-service.
 *
 * Data will be eventually consistent when product service updates occur.
 */
public class Product {
  private final ProductId id;
  private String name;
  private BigDecimal unitPrice;
  private BigDecimal discountPerUnit;
  private String description;
  private boolean available;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private Product(ProductId id) {
    this.id = id;
  }

  public static Product create(CreateProductParams params) {
    Product product = new Product(params.id());
    product.name = params.name();
    product.unitPrice = params.unitPrice();
    product.description = params.description();
    product.available = params.available();
    product.discountPerUnit = params.discountPerUnit() != null ? params.discountPerUnit() : BigDecimal.ZERO;
    product.createdAt = LocalDateTime.now();
    product.updatedAt = LocalDateTime.now();
    return product;
  }

  public static Product reconstruct(ProductReconstructionParams params) {
    Product product = new Product(params.id());
    product.name = params.name();
    product.unitPrice = params.unitPrice();
    product.discountPerUnit = params.discountPerUnit();
    product.description = params.description();
    product.available = params.available();
    product.createdAt = params.createdAt();
    product.updatedAt = params.updatedAt();
    return product;
  }

  // Getters
  public ProductId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public String getDescription() {
    return description;
  }

  public boolean isAvailable() {
    return available;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public record CreateProductParams(
      ProductId id,
      String name,
      BigDecimal unitPrice,
      BigDecimal discountPerUnit,
      String description,
      boolean available) {
  }

  public record ProductReconstructionParams(
      ProductId id,
      String name,
      BigDecimal unitPrice,
      BigDecimal discountPerUnit,
      String description,
      boolean available,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
  }
}
