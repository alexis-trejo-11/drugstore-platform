package microservice.orders.core.models.events;

import microservice.orders.core.models.valueobjects.CustomerId;
import microservice.orders.core.models.valueobjects.Money;
import microservice.orders.core.models.valueobjects.OrderId;

import java.time.LocalDateTime;

public record OrderCreatedEvent(
    OrderId orderId,
    CustomerId customerId,
    Money totalAmount,
    LocalDateTime createdAt
) {
    public OrderCreatedEvent {
        if (orderId == null) throw new IllegalArgumentException("Order ID cannot be null");
        if (customerId == null) throw new IllegalArgumentException("Customer ID cannot be null");
        if (totalAmount == null) throw new IllegalArgumentException("Total amount cannot be null");
        if (createdAt == null) throw new IllegalArgumentException("Created at cannot be null");
    }
}
