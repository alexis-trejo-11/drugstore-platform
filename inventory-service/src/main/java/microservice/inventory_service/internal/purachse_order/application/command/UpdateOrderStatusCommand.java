package microservice.inventory_service.internal.purachse_order.application.command;

import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.OrderStatus;

public record UpdateOrderStatusCommand(PurchaseOrderId purchaseOrderId, OrderStatus newStatus, UserId updatedBy) {
}
