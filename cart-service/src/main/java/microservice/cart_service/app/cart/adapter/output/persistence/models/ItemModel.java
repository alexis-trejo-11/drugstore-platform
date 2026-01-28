package microservice.cart_service.app.cart.adapter.output.persistence.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import microservice.cart_service.app.product.adapter.out.persistence.ProductModel;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class ItemModel {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cart_items_cart_id"))
	protected CartModel cart;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cart_items_product_id"))
	protected ProductModel product;

	@Column(name = "quantity", nullable = false)
	protected int quantity;

	@Column(name = "created_at")
	protected LocalDateTime createdAt;

	@Column(name = "updated_at")
	protected LocalDateTime updatedAt;

	public ItemModel() {
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
