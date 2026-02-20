package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.query;

import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderStatus;
import org.springframework.data.domain.Pageable;

public record GetSaleOrdersByStatus(
        OrderStatus status,
        Pageable pageable
) {
}
