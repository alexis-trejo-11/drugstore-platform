package microservice.inventory_service.external.order.application.command;

import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrderId;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrderStatus;

public record UpdatePurchaseOrderStatusCommand(
        PurchaseOrderId orderId,
        PurchaseOrderStatus newStatus,
        UserId updatedBy
) {
}
