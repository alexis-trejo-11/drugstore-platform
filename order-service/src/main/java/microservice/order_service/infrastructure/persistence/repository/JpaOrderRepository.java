package microservice.order_service.infrastructure.persistence.repository;

import microservice.order_service.domain.models.Order;
import microservice.order_service.infrastructure.persistence.models.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderModel, String> {

    @Query("SELECT o FROM OrderModel o WHERE o.customerId = :customerId AND o.deletedAt IS NULL ORDER BY o.createdAt DESC")
    Page<OrderModel> findByCustomerId(
            @Param("customerId") String customerId,
            Pageable pageable
    );

    @Query("SELECT o FROM OrderModel o WHERE o.customerId = :customerId AND o.status = :status AND o.deletedAt IS NULL ORDER BY o.createdAt DESC")
    Page<OrderModel> findByCustomerIdAndStatus(
            @Param("customerId") String customerId,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("SELECT o FROM OrderModel o WHERE o.customerId = :customerId AND o.createdAt BETWEEN :startDate AND :endDate AND o.deletedAt IS NULL ORDER BY o.createdAt DESC")
    Page<OrderModel> findByCustomerIdAndDateRange(
            @Param("customerId") String customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT o FROM OrderModel o WHERE o.customerId = :customerId AND o.id = :orderId AND o.deletedAt IS NULL")
    Optional<OrderModel> findByCustomerIdAndId(
            @Param("customerId") String customerId,
            @Param("orderId") String orderId
    );

    @Query("SELECT o FROM OrderModel o WHERE o.customerId = :customerId AND o.deletedAt IS NULL ORDER BY o.createdAt DESC")
    List<OrderModel> findRecentOrdersByCustomerId(
            @Param("customerId") String customerId,
            Pageable pageable
    );

    @Query("SELECT COUNT(o) FROM OrderModel o WHERE o.customerId = :customerId AND o.deletedAt IS NULL")
    Long countByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT COUNT(o) FROM OrderModel o WHERE o.customerId = :customerId AND o.status = :status AND o.deletedAt IS NULL")
    Long countByCustomerIdAndStatus(
            @Param("customerId") String customerId,
            @Param("status") String status
    );


    @Query("SELECT o FROM OrderModel o WHERE o.customerId = :customerId AND o.id = :orderId AND o.deletedAt IS NULL")
    Optional<Order> findByCustomerIdAndOrderId(
            @Param("customerId") String customerId,
            @Param("orderId") String orderId
    );
}