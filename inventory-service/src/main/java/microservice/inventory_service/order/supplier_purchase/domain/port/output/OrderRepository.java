package microservice.inventory_service.order.supplier_purchase.domain.port.output;

import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository {
    PurchaseOrder save(PurchaseOrder PurchaseOrder);

    Optional<PurchaseOrder> findById(PurchaseOrderId id);

    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    Page<PurchaseOrder> findBySupplierId(String supplierId, Pageable pageable);

    Page<PurchaseOrder> findByStatus(OrderStatus status, Pageable pageable);

    Page<PurchaseOrder> findByExpectedDeliveryDateBefore(LocalDateTime date, Pageable pageable);

    void delete(PurchaseOrderId id);

    boolean existsById(PurchaseOrderId id);
}
