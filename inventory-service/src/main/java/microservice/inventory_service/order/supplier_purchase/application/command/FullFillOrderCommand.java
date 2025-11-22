package microservice.inventory_service.order.supplier_purchase.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record FullFillOrderCommand(
        PurchaseOrderId purchaseOrderId,
        List<OrderItemCommand> items,
        String deliveryLocation,
        UserId createdBy
) {
}
