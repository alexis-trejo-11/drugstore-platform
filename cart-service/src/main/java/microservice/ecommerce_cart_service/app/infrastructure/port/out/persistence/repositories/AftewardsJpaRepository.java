package microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.repositories;

import microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.models.AfterwardModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AftewardsJpaRepository extends JpaRepository<AfterwardModel, UUID> {
    @Query("SELECT a FROM Afterward a WHERE a.cart.customerId = :customerId AND a.productId = :productId")
    Optional<AfterwardModel> findByClientIdAndProductId(@Param("customerId") UUID clientId, @Param("productId") UUID productId);

    List<AfterwardModel> findByCartId(UUID clientId);
    Page<AfterwardModel> findByCartId(UUID clientId, Pageable pageable);
}
