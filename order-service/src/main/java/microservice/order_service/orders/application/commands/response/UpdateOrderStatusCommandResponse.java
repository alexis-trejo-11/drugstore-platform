package microservice.order_service.orders.application.commands.response;

import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;

import java.time.LocalDateTime;

public record UpdateOrderStatusCommandResponse(
    OrderID orderId,
    String previousStatus,
    String newStatus,
    LocalDateTime updatedAt
) {
    public UpdateOrderStatusCommandResponse {
        if (orderId == null) {
            throw new IllegalArgumentException("OrderID cannot be null");
        }
        if (previousStatus == null || previousStatus.isBlank()) {
            throw new IllegalArgumentException("Previous status cannot be null or empty");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("New status cannot be null or empty");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("UpdatedAt cannot be null");
        }
    }

    public  static UpdateOrderStatusCommandResponse of(Order order, String previousStatus) {
        return new UpdateOrderStatusCommandResponse(order.getId(), previousStatus, order.getStatus().name(), LocalDateTime.now());
    }
}
