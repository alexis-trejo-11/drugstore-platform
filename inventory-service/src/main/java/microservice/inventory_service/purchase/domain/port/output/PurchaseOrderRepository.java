package microservice.inventory_service.purchase.domain.port.output;


import microservice.inventory_service.purchase.domain.entity.PurchaseOrderId;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PurchaseOrderRepository {
    PurchaseOrder save(PurchaseOrder purchaseOrder);

    Optional<PurchaseOrder> findById(PurchaseOrderId id);

    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    Page<PurchaseOrder> findBySupplierId(String supplierId, Pageable pageable);

    Page<PurchaseOrder> findByStatus(PurchaseOrderStatus status, Pageable pageable);

    Page<PurchaseOrder> findByExpectedDeliveryDateBefore(LocalDateTime date, Pageable pageable);

    void delete(PurchaseOrderId id);
}
