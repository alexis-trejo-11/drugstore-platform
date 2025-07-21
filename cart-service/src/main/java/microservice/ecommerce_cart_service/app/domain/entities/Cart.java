package microservice.ecommerce_cart_service.app.domain.entities;

import lombok.*;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CustomerId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.ProductId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    private CartId id;
    private CustomerId customerId;
    private List<CartItem> cartItems = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Cart(CustomerId customerId) {
        this.customerId = customerId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.cartItems = new ArrayList<>();
    }

    public void addItems(List<CartItem> items) {
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        for (var item : items) {
            CartItem existingItem = this.cartItems.stream()
                    .filter(ci -> ci.getProductId().getValue().equals(item.getProductId().getValue()))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                existingItem.updateQuantity(existingItem.getQuantity() + item.getQuantity());
            } else {
                cartItems.add(item);
            }
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void updateItemQuantity(ProductId productId, int newQuantity) {
        CartItem item = cartItems.stream()
                .filter(cartItem -> cartItem.getProductId().getValue().equals(productId.getValue()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item With Product not Found"));

        item.updateQuantity(newQuantity);
    }

    public void removeItems(List<CartItem> cartItems) {
        for (var cartItem : cartItems) {
            cartItems.removeIf(item -> item.getProductId().getValue().equals(cartItem.getProductId().getValue()));
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void clearCart() {
        cartItems.clear();
        this.updatedAt = LocalDateTime.now();
    }

    public void setItems(List<CartItem> items) {
        this.cartItems = items;
    }


    public int getTotalItems() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }


    public List<ProductId> getProductsIdsIn() {
        return cartItems.stream().map(CartItem::getProductId).toList();
    }
}
