package io.github.alexisTrejo11.drugstore.order.orders.application.handler.command;

import io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.*;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.CreateOrderOperationSummary;

public interface OrderCommandHandler {
    CreateOrderOperationSummary handle(CreateDeliveryOrderCommand command);
    CreateOrderOperationSummary handle(CreatePickupOrderCommand command);
    void handle(UpdateOrderAddressCommand command);
    void handle(UpdateOrderDeliverMethodCommand command);
    void handle(DeleteOrderCommand command);
}