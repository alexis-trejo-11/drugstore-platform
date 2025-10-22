package microservice.inventory_service.stock.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrderId;

@Builder
public record ReserveStockCommand(
        InventoryId inventoryId,
        PurchaseOrderId orderId,
        Integer quantity,
        Integer reservationDurationMinutes,
        String reason
) {
}
