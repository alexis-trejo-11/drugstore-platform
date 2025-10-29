package microservice.inventory_service.order.sales.core.application.command;

import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SaleOrderId;

public record SendSaleOrderToDeliveryCommand(SaleOrderId orderId) {
    public static SendSaleOrderToDeliveryCommand of(String orderId) {
        return new SendSaleOrderToDeliveryCommand(new SaleOrderId(orderId));
    }
}
