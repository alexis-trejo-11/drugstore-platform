package io.github.alexisTrejo11.drugstore.order.orders.application.handler.command;

import io.github.alexisTrejo11.drugstore.order.external.address.infrastructure.repository.AddressRepository;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import io.github.alexisTrejo11.drugstore.order.external.address.model.DeliveryAddress;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.CreateOrderOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions.OrderNotFoundIDException;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.Order;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.OrderItem;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.DeliveryInfo;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.PickupInfo;
import io.github.alexisTrejo11.drugstore.order.orders.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandHandlerImpl {
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;

    public CreateOrderOperationSummary handle(CreateDeliveryOrderCommand cmd) {
        DeliveryAddress address = addressRepository.getAddressByIDAndUserID(
            cmd.getAddressID(),
            cmd.getUserID()
        );

        //TODO: Calculate shipping cost and tax amount based on address and items
        Money serviceFee = Money.zero(Order.DEFAULT_CURRENCY);
        Money taxAmount = Money.zero(Order.DEFAULT_CURRENCY);
        List<OrderItem> items = cmd.getItems().stream()
                .map(CreateOrderItemCommand::toEntity)
                .toList();

        Order order = Order.create(cmd.getUserID(), cmd.getDeliveryMethod(), cmd.getNotes(), serviceFee, taxAmount, items);


        // TODO: Calculate shipping cost and estimated delivery date based on address and items
        Money shippingCost = Money.zero(Order.DEFAULT_CURRENCY);
        Money deliveryCost = shippingCost.add(taxAmount).add(serviceFee);
        LocalDateTime estimatedDeliveryDate = LocalDateTime.now().plusDays(5); // Example: 5 days from now

        DeliveryInfo deliveryInfo = DeliveryInfo.create(
                estimatedDeliveryDate,
                shippingCost,
                deliveryCost,
                address
        );

        order.assignDeliveryInfo(deliveryInfo);

        // TODO: Publish Domain Event "OrderCreatedEvent"
        Order orderSaved = orderRepository.save(order);
        return CreateOrderOperationSummary.from(orderSaved);
    }

    public CreateOrderOperationSummary handle(CreatePickupOrderCommand cmd) {
        Money serviceFee = Money.zero(Order.DEFAULT_CURRENCY);
        Money taxAmount = Money.zero(Order.DEFAULT_CURRENCY);
        List<OrderItem> items = cmd.getItems().stream()
                .map(CreateOrderItemCommand::toEntity)
                .toList();

        Order order = Order.create(
            cmd.getUserID(),
            cmd.getDeliveryMethod(),
            cmd.getNotes(), serviceFee,
            taxAmount,
            items);

        String pickupCode = String.format("%06d", new Random().nextInt(999999));
        PickupInfo pickupInfo = PickupInfo.create(cmd.getStoreID(), cmd.getStoreName(), cmd.getStoreAddress(), pickupCode);
        order.assignPickupInfo(pickupInfo);

        Order orderSaved = orderRepository.save(order);
        return CreateOrderOperationSummary.from(orderSaved);
    }

    public void handle(UpdateOrderAddressCommand cmd) {
        Order order = orderRepository.findByID(cmd.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(cmd.orderID()));

        if (!order.getDeliveryMethod().requiresAddress()) {
            throw new IllegalStateException("Cannot update address for orders with delivery method: " + order.getDeliveryMethod());
        }

        DeliveryAddress newAddress = addressRepository.getAddressByIDAndUserID(cmd.addressID(), cmd.userID());

        order.updateDeliveryAddress(newAddress);
        orderRepository.save(order);
    }

    public void handle(UpdateOrderDeliverMethodCommand cmd) {
        Order order = orderRepository.findByID(cmd.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(cmd.orderID()));

        // TODO: CALCULATE new shipping cost and tax amount based on new delivery method and address
        Money newShippingCost = Money.zero(Order.DEFAULT_CURRENCY);
        LocalDateTime newEstimatedDeliveryDate = LocalDateTime.now().plusDays(5); // Example: 5 days from now

        if (cmd.deliveryInfo() != null) {
            order.changeDeliveryMethod(cmd.newMethod(), cmd.deliveryInfo());
        } else if (cmd.pickupInfo() != null) {
            order.changeDeliveryMethod(cmd.newMethod(), cmd.pickupInfo());
        } else {
            throw new IllegalArgumentException("Either deliveryInfo or pickupInfo must be provided based on the new delivery method.");
        }

        orderRepository.save(order);
    }

    public void handle(DeleteOrderCommand cmd) {
        Order order = orderRepository.findByID(cmd.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(cmd.orderID()));

        if (cmd.isHardDelete()) {
            orderRepository.hardDelete(order);
        } else {
            orderRepository.softDelete(order);
        }
    }

    // TODO: Add Crono Job to auto-cancel orders in PENDING status after X days and cancel store pickup orders after Y days
}


