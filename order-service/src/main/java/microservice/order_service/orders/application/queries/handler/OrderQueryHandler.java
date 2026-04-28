package microservice.order_service.orders.application.queries.handler;

import libs_kernel.mapper.ResultMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.order_service.external.address.domain.ports.output.AddressRepository;
import microservice.order_service.external.users.application.service.UserService;
import microservice.order_service.orders.application.exceptions.OrderNotFoundIDException;
import microservice.order_service.orders.application.queries.request.*;
import microservice.order_service.orders.application.queries.response.OrderDetailResult;
import microservice.order_service.orders.application.queries.response.OrderQueryResult;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.ports.output.OrderRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderQueryHandler {
    private final OrderRepository orderRepository;
    private final ResultMapper<OrderQueryResult, Order> orderQueryMapper;
    private final UserService userService;
    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public OrderQueryResult handle(GetOrderByIDQuery query) {
        return orderRepository.findByID(query.orderID())
                .map(orderQueryMapper::toResult)
                .orElseThrow(() -> new OrderNotFoundIDException(query.orderID()));
    }

    @Transactional(readOnly = true)
    public OrderDetailResult handle(GetOrderDetailByIDQuery query) {
        var order = orderRepository.findByID(query.orderID())
                .orElseThrow(() -> new OrderNotFoundIDException(query.orderID()));

        var user = userService.getUserByID(order.getUserID());
        return OrderDetailResult.from(order, user);
    }

    @Transactional(readOnly = true)
    public OrderDetailResult handle(GetOrderByIDAndUserIDQuery query) {
       var order = orderRepository.findByIDAndUserID(query.orderID(), query.userID())
                .orElseThrow(() -> new OrderNotFoundIDException(query.orderID()));

        var user = userService.getUserByID(order.getUserID());
        return OrderDetailResult.from(order, user);
    }

    @Transactional(readOnly = true)
    public Page<OrderQueryResult> handle(GetOrdersByUserIDQuery query) {
        SearchOrdersQuery userOrderQuery = SearchOrdersQuery.builder()
                .userId(query.userID())
                .pageable(query.pagination())
                .build();

        Page<Order> orderPage = orderRepository.findBySpecification(userOrderQuery);

        return orderPage.map(orderQueryMapper::toResult);
    }

    @Transactional(readOnly = true)
    public Page<OrderQueryResult> handle(GetOrdersByUserIDAndStatusQuery query) {
        SearchOrdersQuery userOrderQuery = SearchOrdersQuery.builder()
                .userId(query.userID())
                .status(query.status())
                .pageable(query.pagination().toPageable())
                .build();

        Page<Order> orderPage = orderRepository.findBySpecification(userOrderQuery);
        return orderPage.map(orderQueryMapper::toResult);
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
        return orderPage.map(orderQueryMapper::toResult);
    }

    @Transactional(readOnly = true)
    public Page<OrderQueryResult> handle(SearchOrdersQuery query) {
        Page<Order> orderPage = orderRepository.findBySpecification(query);
        return orderPage.map(orderQueryMapper::toResult);
    }
}