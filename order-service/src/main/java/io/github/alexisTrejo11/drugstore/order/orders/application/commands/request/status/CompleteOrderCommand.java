package io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.status;


import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

public record CompleteOrderCommand(
        OrderID orderID
) {

    public static CompleteOrderCommand of(String orderId) {
        return new CompleteOrderCommand(OrderID.of(orderId));
    }
}
