package microservice.inventory_service.order.sales.core.domain.event;

import microservice.inventory_service.order.sales.core.domain.entity.SaleOrder;

public record CancelOrderSaleEvent(SaleOrder saleOrder) {
}
