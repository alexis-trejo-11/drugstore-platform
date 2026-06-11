package io.github.alexisTrejo11.drugstore.order.orders.application.query.request;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Builder
public record GetOrdersByUserIDAndDateRangeQuery(
        UserID userID, LocalDateTime startDate, LocalDateTime endDate,
        Pageable pagination) {
}

