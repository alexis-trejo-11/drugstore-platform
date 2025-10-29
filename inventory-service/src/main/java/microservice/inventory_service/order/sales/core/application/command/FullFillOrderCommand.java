package microservice.inventory_service.order.sales.core.application.command;

import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SaleOrderId;

public record FullFillOrderCommand(SaleOrderId orderId) {

    public static FullFillOrderCommand of(String orderId) {
        return new FullFillOrderCommand(new SaleOrderId(orderId));
    }
}
