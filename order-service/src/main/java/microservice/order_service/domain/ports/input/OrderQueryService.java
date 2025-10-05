package microservice.order_service.domain.ports.input;

import libs_kernel.page.PageResponse;
import microservice.order_service.application.queries.request.*;
import microservice.order_service.application.queries.response.OrderQueryDetailResult;
import microservice.order_service.application.queries.response.OrderQueryResult;
import microservice.order_service.infrastructure.api.controller.dto.OrderDetailResponse;

import java.util.List;

public interface OrderQueryService {
    OrderQueryResult getOrderById(GetOrderByIdQuery query);
    OrderQueryDetailResult getOrderDetailById(GetOrderDetailByIdQuery query);
     PageResponse<OrderQueryResult> getOrdersByCustomer(GetOrdersByCustomerQuery query);
     PageResponse<OrderQueryResult> getOrdersByCustomerAndStatus(GetOrdersByCustomerAndStatusQuery query);
     PageResponse<OrderQueryResult> getOrdersByCustomerAndDateRange(GetOrdersByCustomerAndDateRangeQuery Query);
    OrderQueryDetailResult getOrderByCustomerAndId(GetOrderByCustomerAndIdQuery query);
    List<OrderQueryResult> getRecentOrdersByCustomer(GetRecentOrdersByCustomerQuery query);
}
