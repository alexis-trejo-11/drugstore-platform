package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query;

import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.OrderStatus;
import org.springframework.data.domain.Pageable;


public record GetOrdersByStatusQuery(OrderStatus status, Pageable pageable) {
}
