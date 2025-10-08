package microservice.order_service.orders.application.commands.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.users.application.service.UserService;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.application.commands.request.*;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.orders.application.exceptions.OrderNotFoundIDException;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.OrderFactory;
import microservice.order_service.orders.domain.models.OrderItem;
import microservice.order_service.orders.domain.ports.output.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandHandler {
    private final OrderRepository orderRepository;
    private final UserService userService;

    public CreateOrderCommandResponse handle(CreateOrderCommand command) {
        command.validate();

        User user = userService.getUserByID(command.userID());
        List<OrderItem> items = command.items().stream()
                .map(CreateOrderCommand.CreateOrderItemCommand::toEntity)
                .toList();

        DeliveryAddress deliveryAddress = null;
        if (command.deliveryMethod().requiresAddress()) {
            deliveryAddress = user.findAddressByID(command.addressID())
                    .orElseThrow(() -> new IllegalArgumentException("Address not found for ID: " + command.addressID()));
        }

        Order order = OrderFactory.createOrder(
                command.deliveryMethod(), command.notes(),
                command.moneyShippingCost(), command.moneyTaxAmount(),
                items,  user,  deliveryAddress
        );

        // TODO: Publish Domain Event "OrderCreatedEvent"
        Order orderSaved = orderRepository.save(order);
        return new CreateOrderCommandResponse(orderSaved.getId(), orderSaved.getStatus().name(), orderSaved.getCreatedAt());
    }

    public void handle(UpdateOrderDeliverMethodCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        order.updateDeliveryMethod(command.newMethod());
        orderRepository.save(order);
    }

    public void handle(UpdateOrderAddressCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        if (!order.getDeliveryMethod().requiresAddress()) {
            throw new IllegalStateException("Cannot update address for orders with delivery method: " + order.getDeliveryMethod());
        }

        DeliveryAddress newAddress = userService.getUserByID(command.userID())
                .findAddressByID(command.addressID())
                .orElseThrow(() -> new IllegalArgumentException("Address not found for ID: " + command.addressID()));

        order.updateDeliveryAddress(newAddress);
        orderRepository.save(order);
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

    // TODO: Add Crono Job to auto-cancel orders in PENDING status after X days and cancel store pickup orders after Y days
}


