package microservice.order_service.orders.application.commands.response;

import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import java.time.LocalDateTime;

public record CancelOrderCommandResponse(
    OrderID orderId,
    String status,
    String cancellationReason,
    LocalDateTime cancelledAt
) {
    public CancelOrderCommandResponse {
        if (orderId == null) {
            throw new IllegalArgumentException("orderID cannot be null");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("status cannot be null or blank");
        }
        if (cancellationReason == null || cancellationReason.isBlank()) {
            throw new IllegalArgumentException("cancellationReason cannot be null or blank");
        }
        if (cancelledAt == null) {
            throw new IllegalArgumentException("cancelledAt cannot be null");
        }
    }

    public static CancelOrderCommandResponse of(
            Order order,
            String cancellationReason
    ) {
        return new CancelOrderCommandResponse(
                order.getId(),
                order.getStatus().toString(),
                cancellationReason,
                order.getUpdatedAt()
        );
    }
}
