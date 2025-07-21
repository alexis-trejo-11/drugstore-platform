package microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.CartItem;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartItemId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.ProductId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cart_items")
public class CartItemModel {
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

    @Column(name = "item_total")
    private BigDecimal itemTotal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemModel cartItem = (CartItemModel) o;
        return Objects.equals(productId, cartItem.productId);
    }



    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }


    public static CartItemModel fromEntity(CartItem cartItem) {
        CartItemModel model = new CartItemModel();
        model.setId(cartItem.getId().getValue());
        model.setCart(new CartModel(cartItem.getId().getValue()));
        model.setProductId(cartItem.getProductId().getValue());
        model.setQuantity(cartItem.getQuantity());
        model.setCreatedAt(cartItem.getCreatedAt());
        model.setUpdatedAt(cartItem.getUpdatedAt());
        return model;
    }


    public CartItem toEntity() {
        CartItem cartItem = new CartItem();
        cartItem.setId(new CartItemId(this.id));
        cartItem.setCartId(new CartId(this.cart.getId()));
        cartItem.setProductId(new ProductId(this.productId));
        cartItem.setQuantity(this.quantity);
        cartItem.setCreatedAt(this.createdAt);
        cartItem.setUpdatedAt(this.updatedAt);
        return cartItem;
    }
}
