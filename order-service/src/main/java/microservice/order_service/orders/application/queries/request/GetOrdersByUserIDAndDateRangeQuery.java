package microservice.order_service.orders.application.queries.request;

import lombok.Builder;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Builder
public record GetOrdersByUserIDAndDateRangeQuery(
        UserID userID, LocalDateTime startDate, LocalDateTime endDate,
        Pageable pagination) {
}

