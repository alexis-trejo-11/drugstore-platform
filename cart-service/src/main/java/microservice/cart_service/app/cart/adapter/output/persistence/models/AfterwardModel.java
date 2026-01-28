package microservice.cart_service.app.cart.adapter.output.persistence.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "afterwards", indexes = {
    @Index(name = "idx_afterwards_cart_id", columnList = "cart_id"),
    @Index(name = "idx_afterwards_product_id", columnList = "product_id"),
    @Index(name = "idx_afterwards_added_at", columnList = "added_at"),
    @Index(name = "idx_afterwards_created_at", columnList = "created_at"),
    @Index(name = "idx_afterwards_cart_product", columnList = "cart_id, product_id")
})
@Getter
@Setter
public class AfterwardModel extends ItemModel {
  @Id
  @Column(name = "id", length = 36)
  private String id;

  @Column(name = "added_at")
  private LocalDateTime addedAt;

}
