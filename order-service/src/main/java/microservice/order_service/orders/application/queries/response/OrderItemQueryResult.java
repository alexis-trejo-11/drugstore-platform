package microservice.order_service.orders.application.queries.response;

import lombok.Builder;
import lombok.Data;
import microservice.order_service.orders.domain.models.OrderItem;
import microservice.order_service.orders.domain.models.valueobjects.ProductID;

import java.math.BigDecimal;

@Builder
public record OrderItemQueryResult(
        ProductID productID,
        String productName,
        Integer quantity,
        BigDecimal subtotal
) {

    public static OrderItemQueryResult from(OrderItem item) {
        if (item == null) return null;

        return OrderItemQueryResult.builder()
                .productID(item.getProductID())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal().amount())
                .build();
    }
}

