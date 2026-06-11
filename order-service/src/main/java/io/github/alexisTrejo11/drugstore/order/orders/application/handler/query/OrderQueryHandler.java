package io.github.alexisTrejo11.drugstore.order.orders.application.handler.query;

import io.github.alexisTrejo11.drugstore.order.external.user.model.User;
import io.github.alexisTrejo11.drugstore.order.external.user.repository.UserRepository;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.request.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions.OrderNotFoundIDException;
import io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions.UserNotFoundException;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderDetailResult;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderQueryResult;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.Order;
import io.github.alexisTrejo11.drugstore.order.orders.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderQueryHandler {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public OrderQueryResult handle(GetOrderByIDQuery query) {
        return orderRepository.findByID(query.orderID())
                .map(OrderQueryResult::toResult)
                .orElseThrow(() -> new OrderNotFoundIDException(query.orderID()));
    }

    @Transactional(readOnly = true)
    public OrderDetailResult handle(GetOrderDetailByIDQuery query) {
        Order order = orderRepository.findByID(query.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(query.orderID()));

        User user = userRepository.findById(order.getUserID())
                .orElseThrow(() -> new UserNotFoundException(order.getUserID()));

        return OrderDetailResult.from(order, user);
    }

    @Transactional(readOnly = true)
    public OrderDetailResult handle(GetOrderByIDAndUserIDQuery query) {
        var order = orderRepository.findByID(query.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(query.orderID()));
        if (order.getOrderTimestamps() != null && order.getOrderTimestamps().getDeletedAt() != null) {
            throw new OrderNotFoundIDException(query.orderID());
        }
        if (order.getUserID() == null || !order.getUserID().equals(query.userID())) {
            throw new OrderNotFoundIDException(query.orderID());
        }

        User user = userRepository.findById(order.getUserID())
                .orElseThrow(() -> new UserNotFoundException(order.getUserID()));
        return OrderDetailResult.from(order, user);
    }

    @Transactional(readOnly = true)
    public Page<OrderQueryResult> handle(GetOrdersByUserIDQuery query) {
        SearchOrdersQuery userOrderQuery = SearchOrdersQuery.builder()
                .userId(query.userID())
                .pageable(query.pagination())
                .build();

        Page<Order> orderPage = orderRepository.findBySpecification(userOrderQuery);

        return orderPage.map(OrderQueryResult::toResult);
    }

    @Transactional(readOnly = true)
    public Page<OrderQueryResult> handle(GetOrdersByUserIDAndStatusQuery query) {
        SearchOrdersQuery userOrderQuery = SearchOrdersQuery.builder()
                .userId(query.userID())
                .status(query.status())
                .pageable(query.pagination().toPageable())
                .build();

        Page<Order> orderPage = orderRepository.findBySpecification(userOrderQuery);
        return orderPage.map(OrderQueryResult::toResult);
    }

    @Transactional(readOnly = true)
    public Page<OrderQueryResult> handle(GetOrdersByUserIDAndDateRangeQuery query) {
        SearchOrdersQuery userOrderQuery = SearchOrdersQuery.builder()
                .userId(query.userID())
                .startDate(query.startDate())
                .endDate(query.endDate())
                .pageable(query.pagination())
                .build();

        Page<Order> orderPage = orderRepository.findBySpecification(userOrderQuery);
        return orderPage.map(OrderQueryResult::toResult);
    }

    @Transactional(readOnly = true)
    public Page<OrderQueryResult> handle(SearchOrdersQuery query) {
        Page<Order> orderPage = orderRepository.findBySpecification(query);
        return orderPage.map(OrderQueryResult::toResult);
    }
}