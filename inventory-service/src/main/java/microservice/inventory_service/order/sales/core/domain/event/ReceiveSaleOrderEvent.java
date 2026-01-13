package microservice.inventory_service.order.sales.core.domain.event;

import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;
import microservice.inventory_service.shared.domain.order.OrderReference;

import java.util.Map;
import java.util.Set;

public record ReceiveSaleOrderEvent(Map<ProductId, Integer> productQuantityMap, OrderReference orderReference) {
}
