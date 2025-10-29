package microservice.inventory_service.order.sales.core.application.command;

import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SaleOrderId;

public record ConfirmSaleOrderCommand(SaleOrderId orderId, String paymentId) {
    public static ConfirmSaleOrderCommand of(String orderId, String paymentId) {
        return new ConfirmSaleOrderCommand(new SaleOrderId(orderId), paymentId);
    }
}
