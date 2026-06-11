package io.github.alexisTrejo11.drugstore.order.orders.application.commands.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.Order;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateOrderOperationSummary {
    private OrderID orderId;
    private String status;
    private LocalDateTime createdAt;


    public static CreateOrderOperationSummary from(Order order) {
        return new CreateOrderOperationSummary(
                order.getId(),
                order.getStatus().name(),
                order.getOrderTimestamps() != null ? order.getOrderTimestamps().getCreatedAt() : null
        );
    }
}
