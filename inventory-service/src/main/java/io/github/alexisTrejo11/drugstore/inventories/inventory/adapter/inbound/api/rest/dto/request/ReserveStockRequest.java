package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.inbound.api.rest.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ReserveStockCommand;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderReference;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveStockRequest {
    @NotBlank
    private String orderId;

    @NotNull
    private OrderReference.OrderType orderType;

    @NotNull
    @Positive
    private Integer quantity;

    private String reason;

    public ReserveStockCommand toCommand(ProductId productId) {
        OrderReference orderReference = new OrderReference(orderType, orderId);
        Map<ProductId, Integer> lines = Map.of(productId, quantity);
        return ReserveStockCommand.builder()
                .orderReference(orderReference)
                .productQuantityMap(lines)
                .reason(reason != null ? reason : "Reserved for order " + orderId)
                .build();
    }
}