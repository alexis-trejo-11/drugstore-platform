package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.command;

import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.valueobject.SaleOrderId;

public record FullFillOrderCommand(SaleOrderId orderId) {

    public static FullFillOrderCommand of(String orderId) {
        return new FullFillOrderCommand(new SaleOrderId(orderId));
    }
}
