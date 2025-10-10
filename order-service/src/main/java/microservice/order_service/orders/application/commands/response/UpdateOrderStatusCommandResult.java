package microservice.order_service.orders.application.commands.response;

import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;

import java.time.LocalDateTime;

public record UpdateOrderStatusCommandResult(
    OrderID orderId,
    String previousStatus,
    String newStatus,
    LocalDateTime updatedAt
) {
    public static UpdateOrderStatusCommandResult of(Order order, String previousStatus) {
        return new UpdateOrderStatusCommandResult(order.getId(), previousStatus, order.getStatus().name(), LocalDateTime.now());
    }
}
