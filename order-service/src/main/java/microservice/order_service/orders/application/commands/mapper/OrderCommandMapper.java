package microservice.order_service.orders.application.commands.mapper;

import microservice.order_service.orders.application.commands.request.CreateDeliveryAddressCommand;
import microservice.order_service.orders.application.commands.request.CreateOrderCommand;
import microservice.order_service.orders.application.commands.request.CreateOrderItemCommand;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.OrderItem;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;

@Component
public class OrderCommandMapper {

    public Order toDomainOrder(CreateOrderCommand command) {
        List<OrderItem> items = command.items().stream()
                .map(this::toDomainOrderItem)
                .toList();

        // TODO: ADD USER
        return Order.create(
                null,
                items,
                command.deliveryMethod(),
                command.addressID(),
                command.notes()
        );

    }

    private OrderItem toDomainOrderItem(CreateOrderItemCommand command) {
        Money unitPrice = new Money(command.unitPrice(), Currency.getInstance("MXN"));
        return OrderItem.builder()
                .productID(command.productId())
                .productName(command.productName())
                .unitPrice(unitPrice)
                .quantity(command.quantity())
                .prescriptionRequired(command.prescriptionRequired() != null ? command.prescriptionRequired() : false)
                .build();
    }

    private DeliveryAddress toDomainDeliveryAddress(CreateDeliveryAddressCommand command) {
        if (command == null) return null;
        return DeliveryAddress.builder()
                .street(command.getStreet())
                .city(command.getCity())
                .state(command.getState())
                .zipCode(command.getPostalCode())
                .country(command.getCountry())
                .additionalInfo(command.getAdditionalInfo())
                .build();
    }

}