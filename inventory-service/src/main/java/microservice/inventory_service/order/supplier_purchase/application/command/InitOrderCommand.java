package microservice.inventory_service.order.supplier_purchase.application.command;

import lombok.Builder;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record InitOrderCommand(
        PurchaseOrderId purchaseOrderId,
        String supplierId,
        String supplierName,
        List<OrderItemCommand> items,
        LocalDateTime expectedDeliveryDate,
        String deliveryLocation,
        UserId createdBy,
        boolean isUpdate
) {
}
