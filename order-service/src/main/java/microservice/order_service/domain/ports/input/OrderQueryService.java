package microservice.order_service.domain.ports.input;

import microservice.order_service.application.queries.response.OrderQueryResponse;
import microservice.order_service.application.queries.response.OrderSummaryQueryResponse;
import microservice.order_service.application.queries.response.PagedOrderSummaryQueryResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderQueryService {
    PagedOrderSummaryQueryResponse getOrdersByCustomer(
            String customerId,
            int page, int size);
    PagedOrderSummaryQueryResponse getOrdersByCustomerAndStatus(
            String customerId,
            String status,
            int page,
            int size);
    PagedOrderSummaryQueryResponse getOrdersByCustomerAndDateRange(
            String customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size);

    OrderQueryResponse getOrderByCustomerAndId(String customerId, String orderId);
    List<OrderSummaryQueryResponse> getRecentOrdersByCustomer(String customerId, int limit);
}
