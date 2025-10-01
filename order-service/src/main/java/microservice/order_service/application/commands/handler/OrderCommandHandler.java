package microservice.order_service.application.commands.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import microservice.order_service.application.commands.mapper.OrderCommandMapper;
import microservice.order_service.application.commands.request.CancelOrderCommand;
import microservice.order_service.application.commands.request.CreateOrderCommand;
import microservice.order_service.application.commands.request.UpdateOrderStatusCommand;
import microservice.order_service.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.application.commands.response.UpdateOrderStatusCommandResponse;
import microservice.order_service.application.exceptions.OrderNotFoundException;
import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.CustomerId;
import microservice.order_service.domain.models.valueobjects.OrderId;
import microservice.order_service.domain.ports.output.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandHandler {
    private final OrderRepository orderRepository;
    private final OrderCommandMapper commandMapper;

    public CreateOrderCommandResponse handle(CreateOrderCommand command) {
        Order order = commandMapper.toDomainOrder(command);
        Order savedOrder = orderRepository.save(order);

        return CreateOrderCommandResponse.builder()
                .orderId(savedOrder.getId())
                .status(savedOrder.getStatus().name())
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }

    public UpdateOrderStatusCommandResponse handle(UpdateOrderStatusCommand command) {
        CustomerId customerId = new CustomerId(command.getCustomerId());
        OrderId orderId = new OrderId(command.getOrderId());

        Order order = orderRepository.findByCustomerIdAndOrderId(customerId, orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + command.getOrderId()));

        OrderStatus previousStatus = order.getStatus();
        OrderStatus newStatus;


        newStatus = OrderStatus.valueOf(command.getNewStatus().toUpperCase());
        order.changeStatus(newStatus);

        Order updatedOrder = orderRepository.save(order);

        return UpdateOrderStatusCommandResponse.builder()
                .orderId(updatedOrder.getId())
                .previousStatus(previousStatus.name())
                .newStatus(updatedOrder.getStatus().name())
                .updatedAt(updatedOrder.getUpdatedAt())
                .build();
    }

    public CancelOrderCommandResponse handle(CancelOrderCommand command) {
        CustomerId customerId = new CustomerId(command.getCustomerId());
        OrderId orderId = new OrderId(command.getOrderId());

        Order order = orderRepository.findByCustomerIdAndOrderId(customerId, orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found: " + command.getOrderId()));

        order.cancel(command.getCancellationReason());

        Order cancelledOrder = orderRepository.save(order);

        return CancelOrderCommandResponse.builder()
                .orderId(cancelledOrder.getId())
                .status(cancelledOrder.getStatus().name())
                .cancellationReason(command.getCancellationReason())
                .cancelledAt(cancelledOrder.getUpdatedAt())
                .build();
    }
}