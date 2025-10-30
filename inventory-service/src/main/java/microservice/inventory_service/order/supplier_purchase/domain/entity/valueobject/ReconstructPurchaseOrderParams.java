package microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject;

import lombok.Builder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrderItem;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReconstructPurchaseOrderParams(
        PurchaseOrderId id,
        String supplierId,
        String supplierName,
        OrderStatus status,
        LocalDateTime orderDate,
        LocalDateTime expectedDeliveryDate,
        LocalDateTime actualDeliveryDate,
        String deliveryLocation,
        UserId createdBy,
        UserId approvedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Integer version,
        List<PurchaseOrderItem> items
) {
}
