package microservice.order_service.orders.infrastructure.persistence.repository;

import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.infrastructure.persistence.models.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderModel, String>, JpaSpecificationExecutor<OrderModel> {
    Page<OrderModel> findAll(Specification<OrderModel> spec, Pageable pageable);

    @Query("SELECT o FROM OrderModel o WHERE o.userID = :userID AND o.id = :orderID AND o.deletedAt IS NULL")
    Optional<OrderModel> findByUserIdAndId(
            @Param("userID") String userID,
            @Param("orderID") String orderId
    );

    @Query("SELECT COUNT(o) FROM OrderModel o WHERE o.userID = :userID AND o.deletedAt IS NULL")
    Long countByUserId(@Param("userID") String userID);

    @Query("SELECT COUNT(o) FROM OrderModel o WHERE o.userID = :userID AND o.status = :status AND o.deletedAt IS NULL")
    Long countByUserIdAndStatus(
            @Param("userID") String userID,
            @Param("status") String status
    );

    @Query(value = "SELECT COUNT(*) > 0 FROM orders o WHERE o.address_id = :addressId AND o.status IN :statuses",
            nativeQuery = true)
    boolean existsByAddressIdAndStatusInNative(@Param("addressId") String addressId,
                                               @Param("statuses") List<String> statuses);
}