package microservice.order_service.application.commands.mapper;

import microservice.order_service.application.commands.request.CreateDeliveryAddressCommand;
import microservice.order_service.application.commands.request.CreateOrderCommand;
import microservice.order_service.application.commands.request.CreateOrderItemCommand;
import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.OrderItem;
import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@Component
public class OrderCommandMapper {

    public Order toDomainOrder(CreateOrderCommand command) {
        OrderId orderId = OrderId.generate();
        CustomerId customerId = new CustomerId(command.getCustomerId());

        List<OrderItem> items = command.getItems().stream()
                .map(this::toDomainOrderItem)
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(item -> item.getSubtotal().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Order.builder()
                .id(orderId)
                .customerId(customerId)
                .items(items)
                .totalAmount(new Money(totalAmount, Currency.getInstance("MXN")))
                .deliveryMethod(DeliveryMethod.valueOf(command.getDeliveryMethod().toUpperCase()))
                .deliveryAddress(toDomainDeliveryAddress(command.getDeliveryAddress()))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .estimatedDeliveryDate(command.getEstimatedDeliveryDate())
                .notes(command.getNotes())
                .build();
    }

    private OrderItem toDomainOrderItem(CreateOrderItemCommand command) {
        ProductId productId = new ProductId(command.getProductId());
        Money unitPrice = new Money(command.getUnitPrice(), Currency.getInstance("MXN"));
        Money subtotal = new Money(
                command.getUnitPrice().multiply(BigDecimal.valueOf(command.getQuantity())),
                Currency.getInstance("MXN")
        );

        return OrderItem.builder()
                .productId(productId)
                .productName(command.getProductName())
                .unitPrice(unitPrice)
                .quantity(command.getQuantity())
                .subtotal(subtotal)
                .prescriptionRequired(command.getPrescriptionRequired() != null ?
                        command.getPrescriptionRequired() : false)
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