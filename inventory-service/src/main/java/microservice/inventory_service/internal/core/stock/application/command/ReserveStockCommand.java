package microservice.inventory_service.internal.core.stock.application.command;

import lombok.Builder;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrderId;

@Builder
public record ReserveStockCommand(
        InventoryId inventoryId,
        PurchaseOrderId orderId,
        Integer quantity,
        Integer reservationDurationMinutes,
        String reason
) {
}
