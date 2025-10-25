package microservice.inventory_service.external.order.infrastructure.adapter.outbound.persistence.repository;


import io.lettuce.core.dynamic.annotation.Param;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderStatus;
import microservice.inventory_service.external.order.infrastructure.adapter.outbound.persistence.model.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, String> {
    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    Page<OrderEntity> findBySupplierId(String supplierId, Pageable pageable);

    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);


    @Query("SELECT p FROM OrderEntity p WHERE p.expectedDeliveryDate < :date")
    Page<OrderEntity> findByExpectedDeliveryDateBefore(@Param("date") LocalDateTime date, Pageable pageable);
}
