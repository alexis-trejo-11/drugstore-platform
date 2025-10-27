package microservice.inventory_service.internal.purachse_order.application.command;

import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.PurchaseOrderId;

public record DeletePurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId,
        boolean hardDelete
) {
}
