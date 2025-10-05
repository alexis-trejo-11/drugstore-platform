package microservice.order_service.domain.models.events;

import microservice.order_service.domain.models.valueobjects.CustomerID;
import microservice.order_service.domain.models.valueobjects.Money;
import microservice.order_service.domain.models.valueobjects.OrderID;

import java.time.LocalDateTime;

public record OrderCreatedEvent(
    OrderID orderId,
    CustomerID customerId,
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
