package microservice.cart_service.app.cart.adapter.output.persistence.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "carts", indexes = {
    @Index(name = "idx_carts_customer_id", columnList = "customer_id"),
    @Index(name = "idx_carts_created_at", columnList = "created_at"),
    @Index(name = "idx_carts_updated_at", columnList = "updated_at")
})
public class CartModel {
  @Id
  @Column(name = "id", length = 36)
  private String id;

  @Column(name = "customer_id", nullable = false, length = 255)
  private String customerId;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<CartItemModel> cartItems;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private Set<AfterwardModel> afterwardItems;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public CartModel(String id) {
    this.id = id;
  }

  @PrePersist
  public void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = createdAt;
  }

  @PreUpdate
  public void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

}
