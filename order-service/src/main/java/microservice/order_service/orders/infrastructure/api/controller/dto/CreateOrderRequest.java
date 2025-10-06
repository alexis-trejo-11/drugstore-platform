package microservice.order_service.orders.infrastructure.api.controller.dto;


import microservice.order_service.orders.application.commands.request.CreateOrderCommand;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        String customerId,
        List<OrderItemRequest> items,
        String deliveryMethod,
        String addressId,
        String notes
) {
    public record OrderItemRequest(
            String productId,
            String productName,
            BigDecimal unitPrice,
            int quantity
    ) {}

    public record AddressRequest(
            String street,
            String city,
            String state,
            String zipCode
    ) {}


    public CreateOrderCommand toCommand() {
        return null;
    }
}