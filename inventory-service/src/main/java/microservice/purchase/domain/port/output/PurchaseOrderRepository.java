package microservice.purchase.domain.port.output;


import microservice.purchase.domain.entity.PurchaseOrder;
import microservice.purchase.domain.entity.PurchaseOrderStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PurchaseOrderRepository {
    PurchaseOrder save(PurchaseOrder purchaseOrder);

    Optional<PurchaseOrder> findByID(String id);

    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    Page<PurchaseOrder> findBySupplierId(String supplierId);

    Page<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

    Page<PurchaseOrder> findByExpectedDeliveryDateBefore(LocalDateTime date);

    Page<PurchaseOrder> findAll();

    void delete(String id);
}
