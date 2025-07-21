package microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.repositories;

import microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.models.CartModel;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

public interface CartJpaRepository extends JpaRepository<CartModel, UUID> {
    boolean existsByCartId(UUID cartId);
    Optional<CartModel> findByCustomerId(UUID customerId);
}
