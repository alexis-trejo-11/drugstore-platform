package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.event;

import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.SaleOrder;

public record FulfillSaleOrderEvent(SaleOrder saleOrder) {
}
