package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.inbound.api.rest.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ReserveStockCommand;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderReference;

import java.util.HashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveStockRequest {
    @NotBlank
    private String orderId;

    @NotNull
    private OrderReference.OrderType orderType;

    private String reason;

    // TODO: UPDATE BASED ON ACTUAL REQUIREMENTS
    public ReserveStockCommand toCommand(String inventoryId) {
        OrderReference orderReference = new OrderReference(orderType, orderId);
        //Map<ProductId, Integer> productQuantityMap = Map.of(new InventoryId(inventoryId), quantity);

        return ReserveStockCommand.builder()
                .orderReference(orderReference)
                .productQuantityMap(new HashMap<>())
                .reason(reason != null ? reason : "Reserved for order " + orderId)
                .build();
    }
}