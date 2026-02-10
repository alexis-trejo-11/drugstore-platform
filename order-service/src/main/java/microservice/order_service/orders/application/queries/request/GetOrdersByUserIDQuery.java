package microservice.order_service.orders.application.queries.request;

import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

    public record GetOrdersByUserIDQuery(UserID userID, Pageable pagination) {
    public GetOrdersByUserIDQuery {
        Objects.requireNonNull(userID, "userID must not be null");
        Objects.requireNonNull(pagination, "pagination must not be null");
    }
}
