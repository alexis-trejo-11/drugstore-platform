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
import microservice.order_service.orders.domain.models.OrderItem;
import microservice.order_service.orders.domain.models.valueobjects.DeliveryInfo;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import microservice.order_service.orders.domain.models.valueobjects.PickupInfo;
import microservice.order_service.orders.domain.ports.output.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandHandlerImpl {
    private final OrderRepository orderRepository;
    private final UserService userService;

    public CreateOrderCommandResponse handle(CreateDeliveryOrderCommand command) {
        User user = userService.getUserByID(command.getUserID());

        //TODO: Calculate shipping cost and tax amount based on address and items
        Money serviceFee = Money.zero(Order.DEFAULT_CURRENCY);
        Money taxAmount = Money.zero(Order.DEFAULT_CURRENCY);
        List<OrderItem> items = command.getItems().stream()
                .map(CreateOrderItemCommand::toEntity)
                .toList();

        Order order = Order.create(command.getUserID(), command.getDeliveryMethod(), command.getNotes(), serviceFee, taxAmount, items);

        DeliveryAddress deliveryAddress = user.findAddressByID(command.getAddressID())
                .orElseThrow(() -> new UserAddressNotFound(command.getAddressID()));

        // TODO: Calculate shipping cost and estimated delivery date based on address and items
        Money shippingCost = Money.zero(Order.DEFAULT_CURRENCY);
        Money deliveryCost = shippingCost.add(taxAmount).add(serviceFee);
        LocalDateTime estimatedDeliveryDate = LocalDateTime.now().plusDays(5); // Example: 5 days from now

        DeliveryInfo deliveryInfo = DeliveryInfo.create(
                estimatedDeliveryDate,
                shippingCost,
                deliveryCost,
                deliveryAddress
        );

        order.assignDeliveryInfo(deliveryInfo);

        // TODO: Publish Domain Event "OrderCreatedEvent"
        Order orderSaved = orderRepository.save(order);
        return CreateOrderCommandResponse.from(orderSaved);
    }

    public CreateOrderCommandResponse handle(CreatePickupOrderCommand command) {
        User user = userService.getUserByID(command.getUserID());
        Money serviceFee = Money.zero(Order.DEFAULT_CURRENCY);
        Money taxAmount = Money.zero(Order.DEFAULT_CURRENCY);
        List<OrderItem> items = command.getItems().stream()
                .map(CreateOrderItemCommand::toEntity)
                .toList();

        Order order = Order.create(command.getUserID(), command.getDeliveryMethod(), command.getNotes(), serviceFee, taxAmount, items);

        String pickupCode = String.format("%06d", new Random().nextInt(999999));
        PickupInfo pickupInfo = PickupInfo.create(command.getStoreID(), command.getStoreName(), command.getStoreAddress(), pickupCode);
        order.assignPickupInfo(pickupInfo);

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

        order.updateDeliveryAddress(newAddress);
        orderRepository.save(order);
    }

    public void handle(UpdateOrderDeliverMethodCommand command) {
        Order order = orderRepository.findByID(command.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(command.orderID()));

        // TODO: CALCULATE new shipping cost and tax amount based on new delivery method and address
        Money newShippingCost = Money.zero(Order.DEFAULT_CURRENCY);
        LocalDateTime newEstimatedDeliveryDate = LocalDateTime.now().plusDays(5); // Example: 5 days from now

        if (command.deliveryInfo() != null) {
            order.changeDeliveryMethod(command.newMethod(), command.deliveryInfo());
        } else if (command.pickupInfo() != null) {
            order.changeDeliveryMethod(command.newMethod(), command.pickupInfo());
        } else {
            throw new IllegalArgumentException("Either deliveryInfo or pickupInfo must be provided based on the new delivery method.");
        }

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


