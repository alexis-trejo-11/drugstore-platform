package microservice.order_service.application.commands.hander;

import lombok.RequiredArgsConstructor;
import microservice.order_service.application.queries.mapper.OrderQueryMapper;
import microservice.order_service.application.queries.request.*;
import microservice.order_service.application.queries.response.OrderQueryResponse;
import microservice.order_service.application.queries.response.OrderSummaryQueryResponse;
import microservice.order_service.application.queries.response.PagedOrderSummaryQueryResponse;
import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.CustomerId;
import microservice.order_service.domain.models.valueobjects.OrderId;
import microservice.order_service.domain.ports.output.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryHandler {

    private final OrderRepository orderRepository;
    private final OrderQueryMapper orderQueryMapper;

    public PagedOrderSummaryQueryResponse handle(GetOrdersByCustomerQuery query) {
        CustomerId customerId = CustomerId.of(query.getCustomerId());
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());

        Page<Order> orderPage = orderRepository.findByCustomerID(customerId, pageable);

        List<OrderSummaryQueryResponse> orderSummaries = orderPage.getContent()
                .stream()
                .map(orderQueryMapper::toOrderSummaryResponse)
                .toList();

        return PagedOrderSummaryQueryResponse.builder()
                .orders(orderSummaries)
                .currentPage(orderPage.getNumber())
                .totalPages(orderPage.getTotalPages())
                .totalElements(orderPage.getTotalElements())
                .size(orderPage.getSize())
                .hasNext(orderPage.hasNext())
                .hasPrevious(orderPage.hasPrevious())
                .build();
    }

    public PagedOrderSummaryQueryResponse handle(GetOrdersByCustomerAndStatusQuery query) {
        CustomerId customerId = CustomerId.of(query.getCustomerId());
        OrderStatus status = OrderStatus.valueOf(query.getStatus().toUpperCase());
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());

        Page<Order> orderPage = orderRepository.findByCustomerIdAndOrderStatus(
                customerId, status, pageable
        );

        List<OrderSummaryQueryResponse> orderSummaries = orderPage.getContent()
                .stream()
                .map(orderQueryMapper::toOrderSummaryResponse)
                .toList();

        return PagedOrderSummaryQueryResponse.builder()
                .orders(orderSummaries)
                .currentPage(orderPage.getNumber())
                .totalPages(orderPage.getTotalPages())
                .totalElements(orderPage.getTotalElements())
                .size(orderPage.getSize())
                .hasNext(orderPage.hasNext())
                .hasPrevious(orderPage.hasPrevious())
                .build();
    }

    public PagedOrderSummaryQueryResponse handle(GetOrdersByCustomerAndDateRangeQuery query) {
        CustomerId customerId = CustomerId.of(query.getCustomerId());
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());

        Page<Order> orderPage = orderRepository.findByCustomerIdAndRangeDate(
                customerId,
                query.getStartDate(),
                query.getEndDate(),
                pageable
        );

        List<OrderSummaryQueryResponse> orderSummaries = orderPage.getContent()
                .stream()
                .map(orderQueryMapper::toOrderSummaryResponse)
                .toList();

        return PagedOrderSummaryQueryResponse.builder()
                .orders(orderSummaries)
                .currentPage(orderPage.getNumber())
                .totalPages(orderPage.getTotalPages())
                .totalElements(orderPage.getTotalElements())
                .size(orderPage.getSize())
                .hasNext(orderPage.hasNext())
                .hasPrevious(orderPage.hasPrevious())
                .build();
    }

    public Optional<OrderQueryResponse> handle(GetOrderByCustomerAndIdQuery query) {
        CustomerId customerId = CustomerId.of(query.getCustomerId());
        OrderId orderId = OrderId.of(query.getOrderId());

        return orderRepository.findByCustomerIdAndOrderId(customerId, orderId)
                .map(orderQueryMapper::toOrderResponse);
    }

    public List<OrderSummaryQueryResponse> handle(GetRecentOrdersByCustomerQuery query) {
        CustomerId customerId = CustomerId.of(query.getCustomerId());

        List<Order> recentOrders = orderRepository.findRecentOrdersByCustomer(
                customerId,
                query.getLimit()
        );

        return recentOrders.stream()
                .map(orderQueryMapper::toOrderSummaryResponse)
                .toList();
    }
}