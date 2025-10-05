package microservice.order_service.application.commands.mapper;

import microservice.order_service.application.commands.request.CreateDeliveryAddressCommand;
import microservice.order_service.application.commands.request.CreateOrderCommand;
import microservice.order_service.application.commands.request.CreateOrderItemCommand;
import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.OrderItem;
import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.valueobjects.*;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;

@Component
public class OrderCommandMapper {

    public Order toDomainOrder(CreateOrderCommand command) {
        List<OrderItem> items = command.items().stream()
                .map(this::toDomainOrderItem)
                .toList();

        return Order.create(
                command.customerId(),
                items,
                command.deliveryMethod(),
                command.addressID(),
                command.notes()
        );

    }

    private OrderItem toDomainOrderItem(CreateOrderItemCommand command) {
        Money unitPrice = new Money(command.unitPrice(), Currency.getInstance("MXN"));
        return OrderItem.builder()
                .productId(command.productId())
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