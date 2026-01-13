package microservice.inventory_service.order.sales.core.domain.event;

import microservice.inventory_service.order.sales.core.domain.entity.SaleOrder;
import microservice.inventory_service.shared.domain.order.OrderStatus;

public record UpdateSaleOrderStatusEvent(SaleOrder saleOrder, OrderStatus newStatus) {
}
