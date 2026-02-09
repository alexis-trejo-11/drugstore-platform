package microservice.cart_service.app.product.adapter.output.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_products_name", columnList = "name"),
    @Index(name = "idx_products_is_available", columnList = "is_available"),
    @Index(name = "idx_products_unit_price", columnList = "unit_price"),
    @Index(name = "idx_products_created_at", columnList = "created_at")
})
@Getter
@Setter
public class ProductModel {

  @Id
  @Column(name = "id", length = 36)
  private String id;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "description", length = 1000)
  private String description;

  @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;

  @Column(name = "discount_per_unit", precision = 10, scale = 2)
  protected BigDecimal discountPerUnit = BigDecimal.ZERO;

  @Column(name = "is_available", nullable = false)
  private boolean isAvailable;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public ProductModel() {
  }

  public ProductModel(String id) {
    this.id = id;
  }

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = createdAt;
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
