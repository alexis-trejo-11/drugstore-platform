package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.event;

import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.SaleOrder;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderStatus;

public record UpdateSaleOrderStatusEvent(SaleOrder saleOrder, OrderStatus newStatus) {
}
