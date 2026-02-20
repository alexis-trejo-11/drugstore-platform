package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public record GetOrderByExpectedDateBeforeQuery(LocalDateTime date, Pageable pageable) {

}
