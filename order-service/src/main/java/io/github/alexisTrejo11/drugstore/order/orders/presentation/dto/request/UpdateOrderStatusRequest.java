package io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.request;

import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.OrderStatus;

public record UpdateOrderStatusRequest(
        OrderStatus newStatus,
        String updatedBy
) {}