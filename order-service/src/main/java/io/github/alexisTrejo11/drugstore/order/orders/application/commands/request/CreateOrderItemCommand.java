package io.github.alexisTrejo11.drugstore.order.orders.application.commands.request;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.OrderItem;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.ProductID;

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
