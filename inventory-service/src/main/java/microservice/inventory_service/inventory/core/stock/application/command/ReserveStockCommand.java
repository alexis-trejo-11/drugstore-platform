package microservice.inventory_service.inventory.core.stock.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;

@Builder
public record ReserveStockCommand(
        InventoryId inventoryId,
        PurchaseOrderId purchaseOrderId,
        Integer quantity,
        Integer reservationDurationMinutes,
        String reason
) {
}
