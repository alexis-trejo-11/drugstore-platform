package microservice.inventory_service.order.sales.core.application.command;

import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SaleOrderId;

public record DeleteSaleOrderCommand(SaleOrderId orderId) {
}
