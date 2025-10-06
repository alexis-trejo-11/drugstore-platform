package microservice.order_service.orders.application.queries.response;

import lombok.Builder;
import lombok.Data;
import microservice.order_service.orders.domain.models.OrderItem;
import microservice.order_service.orders.domain.models.valueobjects.ProductID;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemQueryResult {
    private ProductID productID;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;



    public static OrderItemQueryResult from(OrderItem item) {
        return OrderItemQueryResult.builder()
                .productID(item.getProductID())
                .productName(item.getProductName())
                .unitPrice(item.getUnitPrice().amount())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal().amount())
                .build();
    }
}

