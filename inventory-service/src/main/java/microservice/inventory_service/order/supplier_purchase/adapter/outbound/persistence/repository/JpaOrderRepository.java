package microservice.inventory_service.order.supplier_purchase.adapter.outbound.persistence.repository;


import io.lettuce.core.dynamic.annotation.Param;
import microservice.inventory_service.order.supplier_purchase.adapter.outbound.persistence.model.PurchaseOrderEntityOrder;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaOrderRepository extends JpaRepository<PurchaseOrderEntityOrder, String> {
    Optional<PurchaseOrderEntityOrder> findByOrderNumber(String orderNumber);

    Page<PurchaseOrderEntityOrder> findBySupplierId(String supplierId, Pageable pageable);

    Page<PurchaseOrderEntityOrder> findByStatus(OrderStatus status, Pageable pageable);


    @Query("SELECT p FROM PurchaseOrderEntityOrder p WHERE p.expectedDeliveryDate < :date")
    Page<PurchaseOrderEntityOrder> findByExpectedDeliveryDateBefore(@Param("date") LocalDateTime date, Pageable pageable);
}
