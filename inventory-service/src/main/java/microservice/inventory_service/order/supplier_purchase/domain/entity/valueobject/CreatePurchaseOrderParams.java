package microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject;

import lombok.Builder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrderItem;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CreatePurchaseOrderParams(
        PurchaseOrderId id,
        String supplierId,
        String supplierName,
        List<PurchaseOrderItem> items,
        LocalDateTime expectedDeliveryDate,
        String deliveryLocation,
        UserId createdBy
) {

    public CreatePurchaseOrderParams (
            PurchaseOrderId id,
            String supplierId,
            String supplierName,
            List<PurchaseOrderItem> items,
            LocalDateTime expectedDeliveryDate,
            String deliveryLocation,
            UserId createdBy
    ) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("PurchaseOrder must contain at least one item");
        }

        if (expectedDeliveryDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expected delivery date cannot be in the past");
        }

        if (id == null) {
            throw new IllegalArgumentException("PurchaseOrder ID is required of external entities");
        }

        this.id = id;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.items = List.copyOf(items);
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.deliveryLocation = deliveryLocation;
        this.createdBy = createdBy;
    }
}
