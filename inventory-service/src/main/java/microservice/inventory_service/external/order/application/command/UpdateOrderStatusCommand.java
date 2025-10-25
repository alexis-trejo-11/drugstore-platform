package microservice.inventory_service.external.order.application.command;

import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderStatus;

public record UpdateOrderStatusCommand(OrderId orderId, OrderStatus newStatus, UserId updatedBy) {
}
