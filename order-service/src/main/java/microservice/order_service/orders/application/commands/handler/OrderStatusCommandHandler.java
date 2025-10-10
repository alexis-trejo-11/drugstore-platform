package microservice.order_service.orders.application.commands.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.users.application.service.UserService;
import microservice.order_service.orders.application.commands.request.status.*;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.UpdateOrderStatusCommandResult;
import microservice.order_service.orders.application.exceptions.OrderNotFoundIDException;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.domain.ports.output.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderStatusCommandHandler {
    private final OrderRepository orderRepository;
    private final UserService userService;

    public UpdateOrderStatusCommandResult handle(PrepareOrderCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));
        OrderStatus previousStatus = order.getStatus();

        order.startPreparing();

        Order updatedOrder = orderRepository.save(order);
        return UpdateOrderStatusCommandResult.of(updatedOrder, previousStatus.name());
    }

    public UpdateOrderStatusCommandResult handle(OrderReadyToPickupCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));
        OrderStatus previousStatus = order.getStatus();

        order.readyForPickup();
        Order updatedOrder = orderRepository.save(order);

        return UpdateOrderStatusCommandResult.of(updatedOrder, previousStatus.name());
    }

    public UpdateOrderStatusCommandResult handle(ConfirmOrderCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        OrderStatus previousStatus = order.getStatus();
        order.confirm(command.paymentID(), command.estimatedDeliveryDate());

        Order updatedOrder = orderRepository.save(order);
        return UpdateOrderStatusCommandResult.of(updatedOrder, previousStatus.name());
    }

    public UpdateOrderStatusCommandResult handle(ShipOrderCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        OrderStatus previousStatus = order.getStatus();
        order.markOutForDelivery(command.deliveryTrackNumber());

        Order updatedOrder = orderRepository.save(order);
        return UpdateOrderStatusCommandResult.of(updatedOrder, previousStatus.name());
    }

    public UpdateOrderStatusCommandResult handle(CompleteOrderCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        OrderStatus previousStatus = order.getStatus();
        order.complete();

        Order updatedOrder = orderRepository.save(order);
        return UpdateOrderStatusCommandResult.of(updatedOrder, previousStatus.name());
    }

    public UpdateOrderStatusCommandResult handle(OrderDeliverFailCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        OrderStatus previousStatus = order.getStatus();
        order.returnOrder(command.reason());

        Order updatedOrder = orderRepository.save(order);
        return UpdateOrderStatusCommandResult.of(updatedOrder, previousStatus.name());
    }

    public CancelOrderCommandResponse handle(CancelOrderCommand command) {
        Order order = orderRepository.findByUserIDAndOrderID(command.userID(), command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        order.cancel(command.reason());
        Order cancelledOrder = orderRepository.save(order);

        return CancelOrderCommandResponse.of(cancelledOrder, command.reason());
    }

    // TODO: Add Crono Job to auto-cancel orders in PENDING status after X days and cancel store pickup orders after Y days
}


