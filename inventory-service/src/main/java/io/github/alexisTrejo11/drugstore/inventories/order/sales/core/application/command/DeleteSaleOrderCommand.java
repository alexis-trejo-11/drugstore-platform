package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.command;

import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.valueobject.SaleOrderId;

public record DeleteSaleOrderCommand(SaleOrderId orderId) {
}
