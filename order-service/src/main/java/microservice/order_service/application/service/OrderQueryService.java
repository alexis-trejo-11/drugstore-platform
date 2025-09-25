package microservice.order_service.application.service;

import lombok.RequiredArgsConstructor;
import microservice.order_service.application.exceptions.OrderNotFoundException;
import microservice.order_service.application.commands.hander.OrderQueryHandler;
import microservice.order_service.application.queries.request.*;
import microservice.order_service.application.queries.response.OrderQueryResponse;
import microservice.order_service.application.queries.response.OrderSummaryQueryResponse;
import microservice.order_service.application.queries.response.PagedOrderSummaryQueryResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderQueryHandler queryHandler;

    public PagedOrderSummaryQueryResponse getOrdersByCustomer(
            String customerId,
            int page,
            int size) {

        GetOrdersByCustomerQuery query = GetOrdersByCustomerQuery.builder()
                .customerId(customerId)
                .page(page)
                .size(size)
                .build();

        return queryHandler.handle(query);
    }

    public PagedOrderSummaryQueryResponse getOrdersByCustomerAndStatus(
            String customerId,
            String status,
            int page,
            int size) {

        GetOrdersByCustomerAndStatusQuery query = GetOrdersByCustomerAndStatusQuery.builder()
                .customerId(customerId)
                .status(status)
                .page(page)
                .size(size)
                .build();

        return queryHandler.handle(query);
    }

    public PagedOrderSummaryQueryResponse getOrdersByCustomerAndDateRange(
            String customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size) {

        GetOrdersByCustomerAndDateRangeQuery query = GetOrdersByCustomerAndDateRangeQuery.builder()
                .customerId(customerId)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .build();

        return queryHandler.handle(query);
    }

    public OrderQueryResponse getOrderByCustomerAndId(String customerId, String orderId) {
        GetOrderByCustomerAndIdQuery query = GetOrderByCustomerAndIdQuery.builder()
                .customerId(customerId)
                .orderId(orderId)
                .build();

        return queryHandler.handle(query)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found for customer: " + customerId + " and order: " + orderId));
    }

    public List<OrderSummaryQueryResponse> getRecentOrdersByCustomer(String customerId, int limit) {
        GetRecentOrdersByCustomerQuery query = GetRecentOrdersByCustomerQuery.builder()
                .customerId(customerId)
                .limit(limit)
                .build();

        return queryHandler.handle(query);
    }
}