package microservice.order_service.application.queries.handler;

import libs_kernel.page.PageResponse;
import lombok.RequiredArgsConstructor;
import microservice.order_service.application.queries.mapper.OrderQueryMapper;
import microservice.order_service.application.queries.request.*;
import microservice.order_service.application.queries.response.OrderQueryDetailResult;
import microservice.order_service.application.queries.response.OrderQueryResult;
import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.valueobjects.CustomerID;
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
public class OrderQueryHandler  {
    private final OrderRepository orderRepository;
    private final OrderQueryMapper orderQueryMapper;

    public Optional<OrderQueryResult> handle(GetOrderByIdQuery query) {
         return orderRepository.findById(query.orderID())
                 .map(orderQueryMapper::toOrderSummaryResponse);
    }

    public Optional<OrderQueryDetailResult> handle(GetOrderDetailByIdQuery query) {
        return orderRepository.findById(query.orderID())
                .map(orderQueryMapper::toOrderDetailResponse);
    }

    public Optional<OrderQueryDetailResult> handle(GetOrderByCustomerAndIdQuery query) {
        return orderRepository.findByCustomerIdAndOrderId(query.customerId(), query.orderId())
                .map(orderQueryMapper::toOrderDetailResponse);
    }

    public PageResponse<OrderQueryResult> handle(GetOrdersByCustomerQuery query) {
        Page<Order> orderPage = orderRepository.findByCustomerID(
                query.customerId(),
                query.pagination().toPageable());

        Page<OrderQueryResult> responsePage = orderPage.map(orderQueryMapper::toOrderSummaryResponse);
        return PageResponse.from(responsePage);
    }

    public PageResponse<OrderQueryResult> handle(GetOrdersByCustomerAndStatusQuery query) {
        Page<Order> orderPage = orderRepository.findByCustomerIdAndOrderStatus(
                query.getCustomerId(),
                query.getStatus(),
                query.getPagination().toPageable()
        );

        Page<OrderQueryResult> responsePage = orderPage.map(orderQueryMapper::toOrderSummaryResponse);
        return PageResponse.from(responsePage);
    }

    public PageResponse<OrderQueryResult> handle(GetOrdersByCustomerAndDateRangeQuery query) {
        CustomerID customerId = CustomerID.of(query.getCustomerId());
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());

        Page<Order> orderPage = orderRepository.findByCustomerIdAndRangeDate(
                customerId,
                query.getStartDate(),
                query.getEndDate(),
                pageable
        );

        Page<OrderQueryResult> responsePage = orderPage.map(orderQueryMapper::toOrderSummaryResponse);
        return PageResponse.from(responsePage);
    }



    public List<OrderQueryResult> handle(GetRecentOrdersByCustomerQuery query) {
        List<Order> recentOrders = orderRepository.findRecentOrdersByCustomer(query.getCustomerId(), query.getLimit());

        return recentOrders.stream()
                .map(orderQueryMapper::toOrderSummaryResponse)
                .toList();
    }
}