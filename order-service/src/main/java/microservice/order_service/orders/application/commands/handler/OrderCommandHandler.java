package microservice.order_service.orders.application.commands.handler;

import microservice.order_service.orders.application.commands.request.CreateDeliveryOrderCommand;
import microservice.order_service.orders.application.commands.request.DeleteOrderCommand;
import microservice.order_service.orders.application.commands.request.UpdateOrderAddressCommand;
import microservice.order_service.orders.application.commands.request.UpdateOrderDeliverMethodCommand;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;

public interface OrderCommandHandler {
    CreateOrderCommandResponse handle(CreateDeliveryOrderCommand command);
    void handle(UpdateOrderAddressCommand command);
    void handle(UpdateOrderDeliverMethodCommand command);
    void handle(DeleteOrderCommand command);
}