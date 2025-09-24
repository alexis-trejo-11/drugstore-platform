package microservice.order_service.infrastructure.api.controller.dto;


import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        String customerId,
        List<OrderItemRequest> items,
        String deliveryMethod,
        AddressRequest deliveryAddress,
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
}