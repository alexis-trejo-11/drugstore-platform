package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.query;

import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.valueobject.SaleOrderId;

public record GetSaleOrdersById(
        SaleOrderId orderId
) {

    public static GetSaleOrdersById of(String id) {
        return new GetSaleOrdersById(new SaleOrderId(id));
    }
}
