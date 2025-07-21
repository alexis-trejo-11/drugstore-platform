package microservice.ecommerce_cart_service.app.domain.port.out.repositories;

import microservice.ecommerce_cart_service.app.domain.entities.Cart;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CustomerId;

import java.util.Optional;

public interface CartRepository {
    Cart save(Cart cart);
    Optional<Cart> findById(CartId id);
    Optional<Cart> findByCustomerId(CustomerId customerId);
    void deleteById(CartId id);
    boolean existsById(CartId id);
    boolean existsByClientId(CartId clientId);
}