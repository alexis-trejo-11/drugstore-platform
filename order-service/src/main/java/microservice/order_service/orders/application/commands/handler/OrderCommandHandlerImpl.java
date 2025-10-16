package microservice.order_service.orders.application.commands.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.users.application.service.UserService;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.application.commands.request.*;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.orders.application.exceptions.OrderNotFoundIDException;
import microservice.order_service.orders.application.exceptions.UserAddressNotFound;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.OrderFactory;
import microservice.order_service.orders.domain.models.OrderItem;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import microservice.order_service.orders.domain.ports.output.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandHandlerImpl {
    private final OrderRepository orderRepository;
    private final UserService userService;

    public CreateOrderCommandResponse handle(CreateDeliveryOrderCommand command) {
        if (command.deliveryMethod().requiresAddress()) {
            if (command.addressID() == null) {
                throw new IllegalArgumentException("Address ID is required for delivery methods that require an address.");
            }
        }

        User user = userService.getUserByID(command.userID());
        DeliveryAddress deliveryAddress = user.findAddressByID(command.addressID())
                    .orElseThrow(() -> new UserAddressNotFound(command.addressID()));

        //TODO: Calculate shipping cost and tax amount based on address and items
        Money shippingCost = Money.zero(Order.DEFAULT_CURRENCY);
        Money taxAmount = Money.zero(Order.DEFAULT_CURRENCY);

        Order order = OrderFactory.createDeliveryOrder(
                command.deliveryMethod(),
                command.notes(),
                shippingCost,
                taxAmount,
                command.userID(),
                deliveryAddress.getId()
        );

        List<OrderItem> items = command.items().stream()
                .map(CreateOrderItemCommand::toEntity)
                .toList();

        order.assignItems(items);

        // TODO: Publish Domain Event "OrderCreatedEvent"
        Order orderSaved = orderRepository.save(order);
        return CreateOrderCommandResponse.from(orderSaved);
    }

    public void handle(UpdateOrderAddressCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        if (!order.getDeliveryMethod().requiresAddress()) {
            throw new IllegalStateException("Cannot update address for orders with delivery method: " + order.getDeliveryMethod());
        }

        DeliveryAddress newAddress = userService.getUserByID(command.userID())
                .findAddressByID(command.addressID())
                .orElseThrow(() -> new UserAddressNotFound(command.addressID()));

        order.updateDeliveryAddress(newAddress.getId());
        orderRepository.save(order);
    }

    public void handle(UpdateOrderDeliverMethodCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        // TODO: CALCULATE new shipping cost and tax amount based on new delivery method and address
        Money newShippingCost = Money.zero(Order.DEFAULT_CURRENCY);
        LocalDateTime newEstimatedDeliveryDate = LocalDateTime.now().plusDays(5); // Example: 5 days from now
        order.updateDeliveryMethod(command.newMethod(), command.newAddressID(), newEstimatedDeliveryDate, newShippingCost);
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


