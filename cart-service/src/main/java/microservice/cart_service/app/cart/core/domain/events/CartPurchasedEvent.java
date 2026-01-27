package microservice.cart_service.app.cart.core.domain.events;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class CartPurchasedEvent implements DomainEvent {
    private final CartId cartId;
    private final CustomerId customerId;
    private final List<ProductId> purchasedProducts;
    private final List<ProductId> excludedProducts;
    private final LocalDateTime occurredAt;
    
    public CartPurchasedEvent(CartId cartId, CustomerId customerId, 
                             List<ProductId> purchasedProducts, 
                             List<ProductId> excludedProducts) {
        if (cartId == null) {
            throw new IllegalArgumentException("CartId cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerId cannot be null");
        }
        if (purchasedProducts == null) {
            throw new IllegalArgumentException("Purchased products cannot be null");
        }
        
        this.cartId = cartId;
        this.customerId = customerId;
        this.purchasedProducts = List.copyOf(purchasedProducts);
        this.excludedProducts = excludedProducts != null ? List.copyOf(excludedProducts) : List.of();
        this.occurredAt = LocalDateTime.now();
    }
    
    public CartId getCartId() {
        return cartId;
    }
    
    public CustomerId getCustomerId() {
        return customerId;
    }
    
    public List<ProductId> getPurchasedProducts() {
        return purchasedProducts;
    }
    
    public List<ProductId> getExcludedProducts() {
        return excludedProducts;
    }
    
    @Override
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartPurchasedEvent that = (CartPurchasedEvent) o;
        return cartId.equals(that.cartId) && 
               customerId.equals(that.customerId) && 
               occurredAt.equals(that.occurredAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(cartId, customerId, occurredAt);
    }
    
    @Override
    public String toString() {
        return String.format(
            "CartPurchasedEvent{cartId=%s, customerId=%s, purchasedProducts=%d, excludedProducts=%d, occurredAt=%s}",
            cartId, customerId, purchasedProducts.size(), excludedProducts.size(), occurredAt
        );
    }
}