package microservice.order_service.orders.application.commands.handler;

import microservice.order_service.orders.application.commands.request.*;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;

public interface OrderCommandHandler {
    CreateOrderCommandResponse handle(CreateDeliveryOrderCommand command);
    CreateOrderCommandResponse handle(CreatePickupOrderCommand command);
    void handle(UpdateOrderAddressCommand command);
    void handle(UpdateOrderDeliverMethodCommand command);
    void handle(DeleteOrderCommand command);
}