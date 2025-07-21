package microservice.ecommerce_cart_service.app.domain.entities;

import lombok.*;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.AfterwardItem;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartItemId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.ProductId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    private CartItemId id;
    private CartId cartId;
    private ProductId productId;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CartItem(ProductId productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }



    public static CartItem from(AfterwardItem afterwardItem) {
        return CartItem.builder()
                .id(afterwardItem.getId())
                .productId(afterwardItem.getProductId())
                .quantity(afterwardItem.getQuantity())
                .cartId(afterwardItem.getCartId())
                .createdAt(afterwardItem.getCreatedAt())
                .updatedAt(afterwardItem.getUpdatedAt())
                .build();
    }


    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(productId, cartItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }


}
