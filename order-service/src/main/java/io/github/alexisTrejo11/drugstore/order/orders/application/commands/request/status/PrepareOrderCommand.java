package io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.status;

import jakarta.validation.constraints.NotNull;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

public record PrepareOrderCommand(
       @NotNull OrderID orderID
) {
    public static PrepareOrderCommand of(String orderID) {
        return new PrepareOrderCommand(
                OrderID.of(orderID)
        );
    }
}
