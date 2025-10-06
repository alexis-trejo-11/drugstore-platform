package microservice.order_service.orders.application.queries.request;

import libs_kernel.page.PageInput;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.util.Objects;

    public record GetOrdersByUserIDQuery(UserID userID, PageInput pagination) {
    public GetOrdersByUserIDQuery {
        Objects.requireNonNull(userID, "userID must not be null");
        Objects.requireNonNull(pagination, "pagination must not be null");
    }
}
