package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.query;

import org.springframework.data.domain.Pageable;

public record GetSaleOrdersByCustomerId(
        String customerId,
        Pageable pageable
) {
}
