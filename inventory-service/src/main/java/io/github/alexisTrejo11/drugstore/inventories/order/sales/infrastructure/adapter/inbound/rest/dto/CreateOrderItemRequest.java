package io.github.alexisTrejo11.drugstore.inventories.order.sales.infrastructure.adapter.inbound.rest.dto;

public record CreateOrderItemRequest(
        String id,
        String productId,
        String productName,
        Integer quantity
) {
}
