package microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import microservice.ecommerce_cart_service.app.domain.entities.CartItem;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "afterwards")
public class AfterwardModel {
    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private CartModel cart;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public static AfterwardModel from(AfterwardItem afterwardItem) {
        AfterwardModel model = new AfterwardModel();
        model.setId(afterwardItem.getId().getValue());
        model.setCart(new CartModel(afterwardItem.getCartId().getValue()));
        model.setProductId(afterwardItem.getProductId().getValue());
        model.setQuantity(afterwardItem.getQuantity());
        model.setAddedAt(afterwardItem.getAddedAt());
        model.setCreatedAt(afterwardItem.getCreatedAt());
        model.setUpdatedAt(afterwardItem.getUpdatedAt());

        return model;
    }

    public AfterwardItem toEntity() {
        CartItem cartItem = CartItem.builder()
                .id(new CartItemId(this.id))
                .cartId(new CartId(this.cart.getId()))
                .productId(new ProductId(this.productId))
                .quantity(this.quantity)
                .createdAt(this.addedAt)
                .updatedAt(this.updatedAt)
                .build();

        AfterwardItem  afterwardItem = AfterwardItem.from(cartItem);
        afterwardItem.setAddedAt(this.addedAt);

        return afterwardItem;
    }
}
