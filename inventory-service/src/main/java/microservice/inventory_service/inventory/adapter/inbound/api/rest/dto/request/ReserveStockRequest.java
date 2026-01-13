package microservice.inventory_service.inventory.adapter.inbound.api.rest.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.stock.application.command.ReserveStockCommand;
import microservice.inventory_service.shared.domain.order.OrderReference;

import java.util.HashMap;
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