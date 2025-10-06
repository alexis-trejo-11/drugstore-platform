package microservice.order_service.orders.application.commands.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import microservice.order_service.orders.application.commands.mapper.OrderCommandMapper;
import microservice.order_service.orders.application.commands.request.CancelOrderCommand;
import microservice.order_service.orders.application.commands.request.CreateOrderCommand;
import microservice.order_service.orders.application.commands.request.DeleteOrderCommand;
import microservice.order_service.orders.application.commands.request.UpdateOrderStatusCommand;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.UpdateOrderStatusCommandResponse;
import microservice.order_service.orders.application.exceptions.OrderNotFoundIDException;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.domain.ports.output.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandHandler {
    private final OrderRepository orderRepository;
    private final OrderCommandMapper commandMapper;

    public CreateOrderCommandResponse handle(CreateOrderCommand command) {
        Order order = commandMapper.toDomainOrder(command);

        Order orderSaved = orderRepository.save(order);

        return new CreateOrderCommandResponse(orderSaved.getId(), orderSaved.getStatus().name(), orderSaved.getCreatedAt());
    }

    public UpdateOrderStatusCommandResponse handle(UpdateOrderStatusCommand command) {
        Order order = orderRepository.findByUserIDAndOrderID(command.userID(), command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        OrderStatus previousStatus = order.getStatus();
        OrderStatus newStatus;

        newStatus = OrderStatus.valueOf(command.newStatus().toUpperCase());
        order.changeStatus(newStatus);

        Order updatedOrder = orderRepository.save(order);
        return UpdateOrderStatusCommandResponse.of(updatedOrder, previousStatus.name());
    }

    public CancelOrderCommandResponse handle(CancelOrderCommand command) {
        Order order = orderRepository.findByUserIDAndOrderID(command.userID(), command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        order.cancel(command.reason());
        Order cancelledOrder = orderRepository.save(order);

        return CancelOrderCommandResponse.of(cancelledOrder, command.reason());
    }

    public void handle(DeleteOrderCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        if (command.isHardDelete()) {
            orderRepository.hardDelete(order);
        } else {
            orderRepository.softDelete(order);
        }
    }
}