package microservice.order_service.orders.application.queries.handler;

import libs_kernel.mapper.ResultMapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.orders.application.queries.request.*;
import microservice.order_service.orders.application.queries.response.OrderDetailResult;
import microservice.order_service.orders.application.queries.response.OrderQueryResult;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.ports.output.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryHandler  {
    private final OrderRepository orderRepository;
    private final ResultMapper<OrderQueryResult, Order> orderQueryMapper;

    public Optional<OrderQueryResult> handle(GetOrderByIDQuery query) {
         return orderRepository.findByID(query.orderID())
                 .map(orderQueryMapper::toResult);
    }

    public Optional<OrderDetailResult> handle(GetOrderDetailByIDQuery query) {
        return orderRepository.findByID(query.orderID())
                .map(OrderDetailResult::from);
    }

    public Optional<OrderDetailResult> handle(GetOrderByIDAndUserIDQuery query) {
        return orderRepository.findByUserIDAndOrderID(query.userID(), query.orderID())
                .map(OrderDetailResult::from);
    }

    public Page<OrderQueryResult> handle(GetOrdersByUserIDQuery query) {
        Page<Order> orderPage = orderRepository.findByUserID(
                query.userID(),
                query.pagination().toPageable());

        return orderPage.map(orderQueryMapper::toResult);
    }

    public Page<OrderQueryResult> handle(GetOrdersByUserIDAndStatusQuery query) {
        Page<Order> orderPage = orderRepository.findByUserIDAndOrderStatus(
                query.getUserID(),
                query.getStatus(),
                query.getPagination().toPageable()
        );

        return orderPage.map(orderQueryMapper::toResult);
    }

    public Page<OrderQueryResult> handle(GetOrdersByUserIDAndDateRangeQuery query) {
        Page<Order> orderPage = orderRepository.findByUserIDAndRangeDate(
                query.userID(),
                query.startDate(),
                query.endDate(),
                query.pagination().toPageable()
        );

        return orderPage.map(orderQueryMapper::toResult);

    }
}