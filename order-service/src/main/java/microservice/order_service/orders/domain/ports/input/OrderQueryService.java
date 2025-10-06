package microservice.order_service.orders.domain.ports.input;

import microservice.order_service.orders.application.queries.request.*;
import microservice.order_service.orders.application.queries.response.OrderQueryDetailResult;
import microservice.order_service.orders.application.queries.response.OrderQueryResult;
import org.springframework.data.domain.Page;

public interface OrderQueryService {
    OrderQueryResult getOrderByID(GetOrderByIDQuery query);
    OrderQueryDetailResult getOrderDetailByID(GetOrderDetailByIDQuery query);
    OrderQueryDetailResult getOrderByIDAndUserID(GetOrderByIDAndUserIDQuery query);

    Page<OrderQueryResult> getOrdersByUserID(GetOrdersByUserIDQuery query);
    Page<OrderQueryResult> getOrdersByUserIDAndStatus(GetOrdersByUserIDAndStatusQuery query);
    Page<OrderQueryResult> getOrdersByUserIDAndDateRange(GetOrdersByUserIDAndDateRangeQuery Query);
}
