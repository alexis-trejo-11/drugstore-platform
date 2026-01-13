package microservice.inventory_service.inventory.core.stock.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductQuantity;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.shared.domain.order.OrderReference;

import java.util.Map;
import java.util.Set;

@Builder
public record ReserveStockCommand(
        Map<ProductId, Integer> productQuantityMap,
        OrderReference orderReference,
        String reason
) {
}
