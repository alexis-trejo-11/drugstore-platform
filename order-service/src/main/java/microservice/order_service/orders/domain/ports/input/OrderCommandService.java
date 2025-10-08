package microservice.order_service.orders.domain.ports.input;

import microservice.order_service.orders.application.commands.request.UpdateOrderAddressCommand;
import microservice.order_service.orders.application.commands.request.UpdateOrderDeliverMethodCommand;
import microservice.order_service.orders.application.commands.request.status.*;
import microservice.order_service.orders.application.commands.request.CreateOrderCommand;
import microservice.order_service.orders.application.commands.request.DeleteOrderCommand;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.UpdateOrderStatusCommandResponse;

public interface OrderCommandService {
    CreateOrderCommandResponse createOrder(CreateOrderCommand command);
    void updateDeliveryAddress(UpdateOrderAddressCommand command);
    void updateDeliverMethod(UpdateOrderDeliverMethodCommand command);
    void deleteOrder(DeleteOrderCommand command);

    // Common Status Updates
    UpdateOrderStatusCommandResponse confirmOrder(ConfirmOrderCommand command);
    UpdateOrderStatusCommandResponse startPreparingOrder(PrepareOrderCommand command);
    UpdateOrderStatusCommandResponse completeOrder(CompleteOrderCommand command);

    // Shipping and Delivery
    UpdateOrderStatusCommandResponse shipOrder(ShipOrderCommand command);
    UpdateOrderStatusCommandResponse returnOrder(OrderDeliverFailCommand command);
    CancelOrderCommandResponse cancelOrder(CancelOrderCommand command);

    // Pickup and In-Store Orders
    UpdateOrderStatusCommandResponse readyForPickupOrder(OrderReadyToPickupCommand command);
}
