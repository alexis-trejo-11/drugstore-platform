package microservice.test_service.Repository;

import microservice.test_service.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.clientId = :clientId")
    Optional<Cart> findByClientId(@Param("clientId") Long clientId);
}