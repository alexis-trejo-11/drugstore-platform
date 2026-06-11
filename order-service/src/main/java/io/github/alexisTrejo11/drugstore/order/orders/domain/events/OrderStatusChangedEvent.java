package io.github.alexisTrejo11.drugstore.order.orders.domain.events;

import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.OrderStatus;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

import java.time.LocalDateTime;

public record OrderStatusChangedEvent(
    OrderID orderID,
    OrderStatus oldStatus,
    OrderStatus newStatus,
    LocalDateTime changedAt
) {
    public OrderStatusChangedEvent {
        if (orderID == null) throw new IllegalArgumentException("Order ID cannot be null");
        if (oldStatus == null) throw new IllegalArgumentException("Old status cannot be null");
        if (newStatus == null) throw new IllegalArgumentException("New status cannot be null");
        if (changedAt == null) throw new IllegalArgumentException("Changed at cannot be null");
    }
}
