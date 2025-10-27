package microservice.inventory_service.internal.purachse_order.adapter.outbound.persistence.repository;


import io.lettuce.core.dynamic.annotation.Param;
import microservice.inventory_service.internal.purachse_order.adapter.outbound.persistence.model.PurchaseOrderEntity;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaOrderRepository extends JpaRepository<PurchaseOrderEntity, String> {
    Optional<PurchaseOrderEntity> findByOrderNumber(String orderNumber);

    Page<PurchaseOrderEntity> findBySupplierId(String supplierId, Pageable pageable);

    Page<PurchaseOrderEntity> findByStatus(OrderStatus status, Pageable pageable);


    @Query("SELECT p FROM PurchaseOrderEntity p WHERE p.expectedDeliveryDate < :date")
    Page<PurchaseOrderEntity> findByExpectedDeliveryDateBefore(@Param("date") LocalDateTime date, Pageable pageable);
}
