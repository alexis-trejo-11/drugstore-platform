package microservice.inventory_service.external.order.application.command;

import lombok.Builder;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.ProductId;

import java.math.BigDecimal;

@Builder
public record OrderItemCommand(
        ProductId productId,
        String productName,
        Integer quantity,
        BigDecimal unitCost
) {
}
