package microservice.inventory_service.purchase.application.command;

import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrderId;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrderStatus;

public record UpdatePurchaseOrderStatusCommand(
        PurchaseOrderId orderId,
        PurchaseOrderStatus newStatus,
        UserId updatedBy
) {
}
