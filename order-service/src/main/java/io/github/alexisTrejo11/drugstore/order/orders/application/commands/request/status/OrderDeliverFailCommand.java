package io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.status;

import jakarta.validation.constraints.NotNull;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

public record OrderDeliverFailCommand(
        @NotNull OrderID orderID,
        @NotNull String reason
) {
    public static OrderDeliverFailCommand of(String orderID, String reason) {
        return new OrderDeliverFailCommand(
                OrderID.of(orderID),
                reason
        );
    }
}
