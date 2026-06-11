package io.github.alexisTrejo11.drugstore.order.orders.domain.events;

import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;

import java.time.LocalDateTime;

public record OrderCreatedEvent(
    OrderID orderId,
    UserID userID,
    Money totalAmount,
    LocalDateTime createdAt
) {
    public OrderCreatedEvent {
        if (orderId == null) throw new IllegalArgumentException("Order ID cannot be null");
        if (userID == null) throw new IllegalArgumentException("UserID ID cannot be null");
        if (totalAmount == null) throw new IllegalArgumentException("Total amount cannot be null");
        if (createdAt == null) throw new IllegalArgumentException("Created at cannot be null");
    }
}
