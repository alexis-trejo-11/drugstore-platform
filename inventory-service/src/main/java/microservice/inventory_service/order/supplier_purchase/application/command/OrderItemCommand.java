package microservice.inventory_service.order.supplier_purchase.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;

import java.math.BigDecimal;

@Builder
public record OrderItemCommand(
        String id,
        ProductId productId,
        String productName,
        Integer quantity
) {
}
