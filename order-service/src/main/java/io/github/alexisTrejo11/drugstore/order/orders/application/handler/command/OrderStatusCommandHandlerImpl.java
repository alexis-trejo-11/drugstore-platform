package io.github.alexisTrejo11.drugstore.order.orders.application.handler.command;

import io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.status.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.CancelOrderOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.UpdateOrderStatusOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions.OrderNotFoundIDException;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.Order;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.OrderStatus;
import io.github.alexisTrejo11.drugstore.order.orders.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderStatusCommandHandlerImpl {
    private final OrderRepository orderRepository;

    public UpdateOrderStatusOperationSummary handle(PrepareOrderCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        OrderStatus previousStatus = order.getStatus();

        order.startPreparing();

        Order updatedOrder = orderRepository.save(order);
        return UpdateOrderStatusOperationSummary.of(updatedOrder, previousStatus.name());
    }

    public UpdateOrderStatusOperationSummary handle(OrderReadyToPickupCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));
        OrderStatus previousStatus = order.getStatus();

        order.readyForPickup();
        Order updatedOrder = orderRepository.save(order);

        return UpdateOrderStatusOperationSummary.of(updatedOrder, previousStatus.name());
    }

    public UpdateOrderStatusOperationSummary handle(ConfirmOrderCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        OrderStatus previousStatus = order.getStatus();
        order.confirm(command.paymentID(), command.estimatedDeliveryDate());

        Order updatedOrder = orderRepository.save(order);
        return UpdateOrderStatusOperationSummary.of(updatedOrder, previousStatus.name());
    }

    public UpdateOrderStatusOperationSummary handle(ShipOrderCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        OrderStatus previousStatus = order.getStatus();
        order.markOutForDelivery(command.deliveryTrackNumber());

        Order updatedOrder = orderRepository.save(order);
        return UpdateOrderStatusOperationSummary.of(updatedOrder, previousStatus.name());
    }

    public UpdateOrderStatusOperationSummary handle(CompleteOrderCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        OrderStatus previousStatus = order.getStatus();
        order.complete();

        Order updatedOrder = orderRepository.save(order);
        return UpdateOrderStatusOperationSummary.of(updatedOrder, previousStatus.name());
    }

    public UpdateOrderStatusOperationSummary handle(OrderDeliverFailCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        OrderStatus previousStatus = order.getStatus();
        order.returnOrder(command.reason());

        Order updatedOrder = orderRepository.save(order);
        return UpdateOrderStatusOperationSummary.of(updatedOrder, previousStatus.name());
    }

    public CancelOrderOperationSummary handle(CancelOrderCommand command) {
        Order order = command.isAdminRequest()
                ? orderRepository.findByID(command.orderID())
                    .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()))
                : orderRepository.findByUserIDAndOrderID(command.userID(), command.orderID())
                    .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        order.cancel(command.reason());
        Order cancelledOrder = orderRepository.save(order);

        return CancelOrderOperationSummary.of(cancelledOrder, command.reason());
    }

    // TODO: Add Crono Job to auto-cancel orders in PENDING status after X days and
    // cancel store pickup orders after Y days
}
