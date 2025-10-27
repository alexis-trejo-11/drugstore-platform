package microservice.inventory_service.internal.inventory.core.stock.application.command;

import lombok.Builder;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.PurchaseOrderId;

@Builder
public record ReserveStockCommand(
        InventoryId inventoryId,
        PurchaseOrderId purchaseOrderId,
        Integer quantity,
        Integer reservationDurationMinutes,
        String reason
) {
}
