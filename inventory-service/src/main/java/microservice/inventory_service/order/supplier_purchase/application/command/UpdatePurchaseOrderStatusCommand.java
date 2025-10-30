package microservice.inventory_service.order.supplier_purchase.application.command;

import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.OrderStatus;

public record UpdatePurchaseOrderStatusCommand(PurchaseOrderId purchaseOrderId, OrderStatus newStatus, UserId updatedBy) {
}
