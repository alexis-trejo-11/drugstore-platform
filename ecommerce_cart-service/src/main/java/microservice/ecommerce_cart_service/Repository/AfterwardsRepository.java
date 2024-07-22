package microservice.ecommerce_cart_service.Repository;

import microservice.ecommerce_cart_service.Model.Afterward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface AfterwardsRepository extends JpaRepository<Afterward, Long> {

    @Query("SELECT a FROM Afterward a WHERE a.cart.clientId = :clientId AND a.productId = :productId")
    Optional<Afterward> findByClientIdAndProductId(@Param("clientId") Long clientId, @Param("productId") Long productId);

    List<Afterward> findByClientId(Long clientId);
}