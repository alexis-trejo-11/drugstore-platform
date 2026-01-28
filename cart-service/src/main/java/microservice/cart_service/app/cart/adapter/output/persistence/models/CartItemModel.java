package microservice.cart_service.app.cart.adapter.output.persistence.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "cart_items", indexes = {
    @Index(name = "idx_cart_items_cart_id", columnList = "cart_id"),
    @Index(name = "idx_cart_items_product_id", columnList = "product_id"),
    @Index(name = "idx_cart_items_created_at", columnList = "created_at"),
    @Index(name = "idx_cart_items_cart_product", columnList = "cart_id, product_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_cart_items_cart_product", columnNames = { "cart_id", "product_id" })
})
public class CartItemModel extends ItemModel {
  @Id
  @Column(name = "id", length = 36)
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
