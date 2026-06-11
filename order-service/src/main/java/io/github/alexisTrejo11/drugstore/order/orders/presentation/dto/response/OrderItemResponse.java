package io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response;


import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderItemQueryResult;

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
