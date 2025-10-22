package microservice.stock.application.command;

import lombok.Builder;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.purchase.domain.entity.PurchaseOrderId;

@Builder
public record ReserveStockCommand(
        InventoryId inventoryId,
        PurchaseOrderId orderId,
        Integer quantity,
        Integer reservationDurationMinutes,
        String reason
) {
}
