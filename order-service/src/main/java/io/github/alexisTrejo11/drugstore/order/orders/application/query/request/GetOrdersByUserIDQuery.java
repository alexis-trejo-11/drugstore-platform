package io.github.alexisTrejo11.drugstore.order.orders.application.query.request;

import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

    public record GetOrdersByUserIDQuery(UserID userID, Pageable pagination) {
    public GetOrdersByUserIDQuery {
        Objects.requireNonNull(userID, "userID must not be null");
        Objects.requireNonNull(pagination, "pagination must not be null");
    }
}
