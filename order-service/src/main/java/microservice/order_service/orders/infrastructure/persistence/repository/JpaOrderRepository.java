package microservice.order_service.orders.infrastructure.persistence.repository;

import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.infrastructure.persistence.models.OrderModel;
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

    @Query("SELECT o FROM OrderModel o WHERE o.user.id = :userID AND o.deletedAt IS NULL ORDER BY o.createdAt DESC")
    Page<OrderModel> findByUserId(
            @Param("userID") String userID,
            Pageable pageable
    );

    @Query("SELECT o FROM OrderModel o WHERE o.user.id = :userID AND o.status = :status AND o.deletedAt IS NULL ORDER BY o.createdAt DESC")
    Page<OrderModel> findByUserIdAndStatus(
            @Param("userID") String userID,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("SELECT o FROM OrderModel o WHERE o.user.id = :userID AND o.createdAt BETWEEN :startDate AND :endDate AND o.deletedAt IS NULL ORDER BY o.createdAt DESC")
    Page<OrderModel> findByUserIdAndDateRange(
            @Param("userID") String userID,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT o FROM OrderModel o WHERE o.user.id = :userID AND o.id = :orderID AND o.deletedAt IS NULL")
    Optional<OrderModel> findByUserIdAndId(
            @Param("userID") String userID,
            @Param("orderID") String orderId
    );

    @Query("SELECT o FROM OrderModel o WHERE o.user.id = :userID AND o.deletedAt IS NULL ORDER BY o.createdAt DESC")
    List<OrderModel> findRecentOrdersByUserId(
            @Param("userID") String userID,
            Pageable pageable
    );

    @Query("SELECT COUNT(o) FROM OrderModel o WHERE o.user.id = :userID AND o.deletedAt IS NULL")
    Long countByUserId(@Param("userID") String userID);

    @Query("SELECT COUNT(o) FROM OrderModel o WHERE o.user.id = :userID AND o.status = :status AND o.deletedAt IS NULL")
    Long countByUserIdAndStatus(
            @Param("userID") String userID,
            @Param("status") String status
    );


    @Query("SELECT o FROM OrderModel o WHERE o.user.id = :userID AND o.id = :orderID AND o.deletedAt IS NULL")
    Optional<Order> findByUserIdAndOrderId(
            @Param("userID") String userID,
            @Param("orderID") String orderId
    );
}