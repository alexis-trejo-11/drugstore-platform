package microservice.order_service.domain.ports.input;

import microservice.order_service.application.queries.request.*;
import microservice.order_service.application.queries.response.OrderQueryResponse;
import microservice.order_service.application.queries.response.OrderSummaryQueryResponse;
import microservice.order_service.application.queries.response.PagedOrderSummaryQueryResponse;

import java.util.List;

public interface OrderQueryService {
     PagedOrderSummaryQueryResponse getOrdersByCustomer(GetOrdersByCustomerQuery query);
     PagedOrderSummaryQueryResponse getOrdersByCustomerAndStatus(GetOrdersByCustomerAndStatusQuery query);
     PagedOrderSummaryQueryResponse getOrdersByCustomerAndDateRange(GetOrdersByCustomerAndDateRangeQuery Query);
     OrderQueryResponse getOrderByCustomerAndId(GetOrderByCustomerAndIdQuery query);
    List<OrderSummaryQueryResponse> getRecentOrdersByCustomer(GetRecentOrdersByCustomerQuery query);
}
