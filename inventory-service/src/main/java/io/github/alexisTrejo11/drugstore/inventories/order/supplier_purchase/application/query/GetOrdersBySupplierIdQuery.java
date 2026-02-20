package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query;

import org.springframework.data.domain.Pageable;

public record GetOrdersBySupplierIdQuery(String supplierId, Pageable pageable) {
}
