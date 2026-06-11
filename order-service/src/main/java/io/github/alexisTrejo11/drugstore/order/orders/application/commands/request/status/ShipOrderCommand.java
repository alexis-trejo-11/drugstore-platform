package io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.status;

import jakarta.validation.constraints.NotNull;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

public record ShipOrderCommand(
        @NotNull OrderID orderID,
        @NotNull String deliveryTrackNumber

) {
    public static ShipOrderCommand of(String orderID, String deliveryTrackNumber) {
        return new ShipOrderCommand(
                OrderID.of(orderID),
                deliveryTrackNumber
        );
    }
}
