package io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.status;

import jakarta.validation.constraints.NotNull;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

public record OrderReadyToPickupCommand(
        @NotNull OrderID orderID
) {
    public static OrderReadyToPickupCommand of(String orderId) {
        return new OrderReadyToPickupCommand(OrderID.of(orderId));
    }
}
