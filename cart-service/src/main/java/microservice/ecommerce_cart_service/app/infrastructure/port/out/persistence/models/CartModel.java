package microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.Cart;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CustomerId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "carts")
public class CartModel {
    @Id
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItemModel> cartItems;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public CartModel(UUID id) {
        this.id = id;
    }


    public static CartModel fromEntity(Cart cart) {
        CartModel model = new CartModel(cart.getId().getValue());
        model.setClientId(cart.getCustomerId().getValue());
        model.setCreatedAt(cart.getCreatedAt());
        model.setUpdatedAt(cart.getUpdatedAt());
        return model;
    }

    public Cart toEntity() {
        Cart cart = new Cart();
        cart.setId(new CartId(this.id));
        cart.setCustomerId(new CustomerId(this.clientId));
        cart.setCreatedAt(this.createdAt);
        cart.setUpdatedAt(this.updatedAt);
        cart.setCartItems(new ArrayList<>());
        return cart;
    }
}


