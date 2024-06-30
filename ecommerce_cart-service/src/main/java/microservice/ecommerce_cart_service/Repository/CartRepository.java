package microservice.ecommerce_cart_service.Repository;

import microservice.ecommerce_cart_service.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByClientId(Long userId);
}