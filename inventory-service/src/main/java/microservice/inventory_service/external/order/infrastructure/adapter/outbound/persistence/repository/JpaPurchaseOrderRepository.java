package microservice.inventory_service.external.order.infrastructure.adapter.outbound.persistence.repository;


import io.lettuce.core.dynamic.annotation.Param;
import microservice.inventory_service.external.order.domain.entity.valueobject.PurchaseOrderStatus;
import microservice.inventory_service.external.order.infrastructure.adapter.outbound.persistence.model.PurchaseOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaPurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, String> {
    Optional<PurchaseOrderEntity> findByOrderNumber(String orderNumber);

    Page<PurchaseOrderEntity> findBySupplierId(String supplierId, Pageable pageable);

    Page<PurchaseOrderEntity> findByStatus(PurchaseOrderStatus status, Pageable pageable);


    @Query("SELECT p FROM PurchaseOrderEntity p WHERE p.expectedDeliveryDate < :date")
    Page<PurchaseOrderEntity> findByExpectedDeliveryDateBefore(@Param("date") LocalDateTime date, Pageable pageable);
}
