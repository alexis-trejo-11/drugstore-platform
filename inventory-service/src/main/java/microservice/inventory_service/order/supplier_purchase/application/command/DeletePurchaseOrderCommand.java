package microservice.inventory_service.order.supplier_purchase.application.command;

import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;

public record DeletePurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId,
        boolean hardDelete
) {
}
