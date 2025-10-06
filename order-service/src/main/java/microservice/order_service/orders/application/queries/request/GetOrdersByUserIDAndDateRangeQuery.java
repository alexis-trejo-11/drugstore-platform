package microservice.order_service.orders.application.queries.request;

import libs_kernel.page.PageInput;
import lombok.Builder;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.time.LocalDateTime;

@Builder
public record GetOrdersByUserIDAndDateRangeQuery(
        UserID userID, LocalDateTime startDate, LocalDateTime endDate,
        PageInput pagination) {
}

