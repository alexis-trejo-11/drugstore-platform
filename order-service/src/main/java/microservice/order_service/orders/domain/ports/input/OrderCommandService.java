package microservice.order_service.orders.domain.ports.input;

import microservice.order_service.orders.application.commands.request.*;
import microservice.order_service.orders.application.commands.request.status.*;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.UpdateOrderStatusCommandResult;

public interface OrderCommandService {
    CreateOrderCommandResponse createDeliveryOrder(CreateDeliveryOrderCommand command);
    CreateOrderCommandResponse createPickupOrder(CreatePickupOrderCommand command);
    void updateDeliveryAddress(UpdateOrderAddressCommand command);
    void updateDeliverMethod(UpdateOrderDeliverMethodCommand command);
    void deleteOrder(DeleteOrderCommand command);

    // Common Status Updates
    UpdateOrderStatusCommandResult confirmOrder(ConfirmOrderCommand command);
    UpdateOrderStatusCommandResult startPreparingOrder(PrepareOrderCommand command);
    UpdateOrderStatusCommandResult completeOrder(CompleteOrderCommand command);

    // Shipping and Delivery
    UpdateOrderStatusCommandResult shipOrder(ShipOrderCommand command);
    UpdateOrderStatusCommandResult returnOrder(OrderDeliverFailCommand command);
    CancelOrderCommandResponse cancelOrder(CancelOrderCommand command);

    // Pickup and In-Store Orders
    UpdateOrderStatusCommandResult readyForPickupOrder(OrderReadyToPickupCommand command);
}
