package microservice.order_service.orders.application.queries.request;

import libs_kernel.page.PageInput;
import lombok.Builder;
import lombok.Data;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

@Data
@Builder
public class GetOrdersByUserIDAndStatusQuery {
    private final UserID userID;
    private final OrderStatus status;
    private final PageInput  pagination;
}
