package io.github.alexisTrejo11.drugstore.order.orders.application.commands.response;

import io.github.alexisTrejo11.drugstore.order.orders.domain.models.Order;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;
import java.time.LocalDateTime;

public record CancelOrderOperationSummary(
    OrderID orderId,
    String status,
    String cancellationReason,
    LocalDateTime cancelledAt
) {
    public CancelOrderOperationSummary {
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

    public static CancelOrderOperationSummary of(
            Order order,
            String cancellationReason
    ) {
        LocalDateTime cancelledAt = order.getOrderTimestamps() != null
                ? order.getOrderTimestamps().getUpdatedAt()
                : null;
        if (cancelledAt == null) {
            cancelledAt = LocalDateTime.now();
        }
        return new CancelOrderOperationSummary(
                order.getId(),
                order.getStatus().toString(),
                cancellationReason,
                cancelledAt
        );
    }
}
