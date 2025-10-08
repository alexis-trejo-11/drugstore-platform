package microservice.order_service.orders.infrastructure.api.dto;

import microservice.order_service.orders.application.commands.request.CreateOrderCommand;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.ProductID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        String userID,
        String addressId,
        DeliveryMethod deliveryMethod,
        BigDecimal shippingCost,
        BigDecimal taxAmount,
        String currency,
        String notes,
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            String productId,
            String productName,
            BigDecimal subtotal,
            int quantity,
            String currency,
            boolean isPrescriptionRequired
    ) {}

    public CreateOrderCommand toCommand() {
        return CreateOrderCommand.builder()
                .userID(userID != null ? new UserID(userID) : null)
                .addressID(addressId != null ? new AddressID(addressId) : null)
                .deliveryMethod(deliveryMethod)
                .shippingCost(shippingCost)
                .taxAmount(taxAmount)
                .currency(currency)
                .notes(notes)
                .items(items != null ? items.stream().map(item -> new CreateOrderCommand.CreateOrderItemCommand(
                       item.productId !=  null ? new ProductID(item.productId) : null,
                        item.productName != null ? item.productName : null,
                        item.subtotal != null ? item.subtotal : BigDecimal.ZERO,
                        Math.max(item.quantity, 0),
                        item.currency,
                        item.isPrescriptionRequired
                )).toList() : List.of())
                .build();
    }
}