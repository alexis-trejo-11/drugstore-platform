package microservice.order_service.orders.application.service;

import lombok.RequiredArgsConstructor;
import microservice.order_service.orders.application.commands.request.DeleteOrderCommand;
import microservice.order_service.orders.application.exceptions.OrderNotFoundException;
import microservice.order_service.orders.application.queries.handler.OrderQueryHandler;
import microservice.order_service.orders.application.commands.request.CancelOrderCommand;
import microservice.order_service.orders.application.commands.request.CreateOrderCommand;
import microservice.order_service.orders.application.commands.request.UpdateOrderStatusCommand;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.UpdateOrderStatusCommandResponse;
import microservice.order_service.orders.application.commands.handler.OrderCommandHandler;
import microservice.order_service.orders.application.exceptions.OrderNotFoundIDException;
import microservice.order_service.orders.application.queries.request.*;
import microservice.order_service.orders.application.queries.response.OrderQueryDetailResult;
import microservice.order_service.orders.application.queries.response.OrderQueryResult;
import microservice.order_service.orders.domain.ports.input.OrderApplicationFacade;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderApplicationFacadeImpl implements OrderApplicationFacade {
    private final OrderCommandHandler commandHandler;
    private final OrderQueryHandler queryHandler;

    @Override
    public CreateOrderCommandResponse createOrder(CreateOrderCommand command) {
        return commandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusCommandResponse updateOrderStatus(UpdateOrderStatusCommand command) {
        return commandHandler.handle(command);
    }

    @Override
    public CancelOrderCommandResponse cancelOrder(CancelOrderCommand command) {
        return commandHandler.handle(command);
    }

    @Override
    public void deleteOrder(DeleteOrderCommand command) {
        commandHandler.handle(command);
    }

    @Override
    public OrderQueryResult getOrderByID(GetOrderByIDQuery query) {
        return queryHandler.handle(query)
                .orElseThrow(() -> new OrderNotFoundIDException(query.orderID()));
    }

    @Override
    public OrderQueryDetailResult getOrderDetailByID(GetOrderDetailByIDQuery query) {
        return queryHandler.handle(query)
                .orElseThrow(() -> new OrderNotFoundIDException(query.orderID()));
    }

    @Override
    public Page<OrderQueryResult> getOrdersByUserID(GetOrdersByUserIDQuery query) {
        return queryHandler.handle(query);
    }

    @Override
    public Page<OrderQueryResult> getOrdersByUserIDAndStatus(GetOrdersByUserIDAndStatusQuery query) {
        return queryHandler.handle(query);
    }

    @Override
    public Page<OrderQueryResult> getOrdersByUserIDAndDateRange(GetOrdersByUserIDAndDateRangeQuery Query) {
        return queryHandler.handle(Query);
    }

    @Override
    public OrderQueryDetailResult getOrderByIDAndUserID(GetOrderByIDAndUserIDQuery query) {
        return queryHandler.handle(query)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found for user: " + query.userID() + " and order: " + query.orderID()));
    }


}