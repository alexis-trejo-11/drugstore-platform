package io.github.alexisTrejo11.drugstore.order.orders.application.query.request;

import jakarta.validation.constraints.NotNull;
import libs_kernel.page.PageRequest;
import lombok.Builder;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.OrderStatus;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;

@Builder
public record GetOrdersByUserIDAndStatusQuery(
        @NotNull UserID userID,
        @NotNull OrderStatus status,
        @NotNull PageRequest pagination
) {}
