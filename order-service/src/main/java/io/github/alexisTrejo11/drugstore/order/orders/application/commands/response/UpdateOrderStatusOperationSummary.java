package io.github.alexisTrejo11.drugstore.order.orders.application.commands.response;

import io.github.alexisTrejo11.drugstore.order.orders.domain.models.Order;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

import java.time.LocalDateTime;

public record UpdateOrderStatusOperationSummary(
    OrderID orderId,
    String previousStatus,
    String newStatus,
    LocalDateTime updatedAt
) {
    public static UpdateOrderStatusOperationSummary of(Order order, String previousStatus) {
        return new UpdateOrderStatusOperationSummary(order.getId(), previousStatus, order.getStatus().name(), LocalDateTime.now());
    }
}
