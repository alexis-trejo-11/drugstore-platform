package microservice.order_service.orders.application.queries.request;

import jakarta.validation.constraints.NotNull;
import libs_kernel.page.PageInput;
import lombok.Builder;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

@Builder
public record GetOrdersByUserIDAndStatusQuery(
        @NotNull UserID userID,
        @NotNull OrderStatus status,
        @NotNull PageInput pagination
) {}
