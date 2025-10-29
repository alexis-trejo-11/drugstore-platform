package microservice.inventory_service.order.sales.core.application.command;

import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SaleOrderId;

public record CancelSaleOrderCommand(SaleOrderId orderId, String reason) {
    public static CancelSaleOrderCommand of(String orderId, String reason) {
        return new CancelSaleOrderCommand(new SaleOrderId(orderId), reason);
    }
}
