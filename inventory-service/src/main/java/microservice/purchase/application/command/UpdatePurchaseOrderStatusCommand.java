package microservice.purchase.application.command;

import microservice.inventory.domain.entity.valueobject.id.UserId;
import microservice.purchase.domain.entity.PurchaseOrderId;
import microservice.purchase.domain.entity.PurchaseOrderStatus;

public record UpdatePurchaseOrderStatusCommand(
        PurchaseOrderId orderId,
        PurchaseOrderStatus newStatus,
        UserId updatedBy
) {
}
