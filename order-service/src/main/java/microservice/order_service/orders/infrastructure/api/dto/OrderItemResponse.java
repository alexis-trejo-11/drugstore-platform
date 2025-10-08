package microservice.order_service.orders.infrastructure.api.dto;


import microservice.order_service.orders.application.queries.response.OrderItemQueryResult;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productID,
        String productName,
        BigDecimal subtotal,
        int quantity
) {
    public static  OrderItemResponse from(OrderItemQueryResult result) {
        return new OrderItemResponse(
                result.productID() != null ? result.productID().value() : null,
                result.productName(),
                result.subtotal(),
                result.quantity()
        );
    }
}
