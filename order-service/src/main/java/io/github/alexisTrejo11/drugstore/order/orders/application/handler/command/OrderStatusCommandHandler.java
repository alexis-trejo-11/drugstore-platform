package io.github.alexisTrejo11.drugstore.order.orders.application.handler.command;

import io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.status.*;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.CancelOrderOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.UpdateOrderStatusOperationSummary;

public interface OrderStatusCommandHandler {
    UpdateOrderStatusOperationSummary handle(PrepareOrderCommand command);

    UpdateOrderStatusOperationSummary handle(OrderReadyToPickupCommand command);

    UpdateOrderStatusOperationSummary handle(ConfirmOrderCommand command);

    UpdateOrderStatusOperationSummary handle(ShipOrderCommand command);

    UpdateOrderStatusOperationSummary handle(CompleteOrderCommand command);

    UpdateOrderStatusOperationSummary handle(OrderDeliverFailCommand command);

    CancelOrderOperationSummary handle(CancelOrderCommand command);
}
