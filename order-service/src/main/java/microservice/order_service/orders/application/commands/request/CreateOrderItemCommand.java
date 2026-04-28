package microservice.order_service.orders.application.commands.request;

import lombok.Builder;
import microservice.order_service.orders.domain.models.OrderItem;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import microservice.order_service.orders.domain.models.valueobjects.ProductID;

import java.math.BigDecimal;
import java.util.Currency;

@Builder
public record CreateOrderItemCommand(
        ProductID productID,
        String productName,
        Money subtotal,
        Integer quantity,
        Boolean isPrescriptionRequired
) {
    public OrderItem toEntity() {
        return OrderItem.create(
                this.productID,
                this.productName,
                subtotal,
                this.quantity,
                this.isPrescriptionRequired
        );
    }
}
