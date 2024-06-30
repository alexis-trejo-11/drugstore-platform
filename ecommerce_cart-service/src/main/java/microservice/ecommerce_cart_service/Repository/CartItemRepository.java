package microservice.ecommerce_cart_service.Repository;

import microservice.ecommerce_cart_service.Model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

    public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}