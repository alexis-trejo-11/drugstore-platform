package microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.repositories;

import microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.models.CartItemModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartItemJpaRepository extends JpaRepository<CartItemModel, UUID> {
    List<CartItemModel> findByCartIdAndProductIdIn(UUID cartId, List<UUID> productIds);
    List<CartItemModel> findByCartId(UUID cartId);
    Page<CartItemModel> findByCartId(UUID cartId, Pageable pageable);
}
