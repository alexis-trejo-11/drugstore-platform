package microservice.order_service.orders.application.commands.handler;

import microservice.order_service.orders.application.commands.request.status.*;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.UpdateOrderStatusCommandResult;

public interface OrderStatusCommandHandler {
    UpdateOrderStatusCommandResult handle(PrepareOrderCommand command);
    UpdateOrderStatusCommandResult handle(OrderReadyToPickupCommand command);
    UpdateOrderStatusCommandResult handle(ConfirmOrderCommand command);
    UpdateOrderStatusCommandResult handle(ShipOrderCommand command);
    UpdateOrderStatusCommandResult handle(CompleteOrderCommand command);
    UpdateOrderStatusCommandResult handle(OrderDeliverFailCommand command);
    CancelOrderCommandResponse handle(CancelOrderCommand command);
}
