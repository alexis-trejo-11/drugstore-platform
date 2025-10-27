package microservice.inventory_service.internal.purachse_order.application.command;

import lombok.Builder;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.ProductId;

import java.math.BigDecimal;

@Builder
public record OrderItemCommand(
        String id,
        ProductId productId,
        String productName,
        Integer quantity,
        BigDecimal unitCost
) {
}
