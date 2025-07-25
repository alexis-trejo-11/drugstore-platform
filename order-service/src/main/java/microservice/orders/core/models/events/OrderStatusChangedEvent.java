package microservice.orders.core.models.events;

import microservice.orders.core.models.enums.OrderStatus;
import microservice.orders.core.models.valueobjects.OrderId;

import java.time.LocalDateTime;

public record OrderStatusChangedEvent(
    OrderId orderId,
    OrderStatus oldStatus,
    OrderStatus newStatus,
    LocalDateTime changedAt
) {
    public OrderStatusChangedEvent {
        if (orderId == null) throw new IllegalArgumentException("Order ID cannot be null");
        if (oldStatus == null) throw new IllegalArgumentException("Old status cannot be null");
        if (newStatus == null) throw new IllegalArgumentException("New status cannot be null");
        if (changedAt == null) throw new IllegalArgumentException("Changed at cannot be null");
    }
}
