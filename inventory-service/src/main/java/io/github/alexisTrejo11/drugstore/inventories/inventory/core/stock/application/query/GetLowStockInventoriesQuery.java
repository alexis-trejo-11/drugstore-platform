package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.query;

import org.springframework.data.domain.Pageable;

public record GetLowStockInventoriesQuery(Pageable pagination) {
}
