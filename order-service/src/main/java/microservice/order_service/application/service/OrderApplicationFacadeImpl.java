package microservice.order_service.application.service;

import libs_kernel.page.PageResponse;
import lombok.RequiredArgsConstructor;
import microservice.order_service.application.commands.request.DeleteOrderCommand;
import microservice.order_service.application.exceptions.OrderNotFoundException;
import microservice.order_service.application.queries.handler.OrderQueryHandler;
import microservice.order_service.application.commands.request.CancelOrderCommand;
import microservice.order_service.application.commands.request.CreateOrderCommand;
import microservice.order_service.application.commands.request.UpdateOrderStatusCommand;
import microservice.order_service.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.application.commands.response.UpdateOrderStatusCommandResponse;
import microservice.order_service.application.commands.handler.OrderCommandHandler;
import microservice.order_service.application.exceptions.OrderNotFoundIDException;
import microservice.order_service.application.queries.request.*;
import microservice.order_service.application.queries.response.OrderQueryDetailResult;
import microservice.order_service.application.queries.response.OrderQueryResult;
import microservice.order_service.domain.ports.input.OrderApplicationFacade;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderApplicationFacadeImpl implements OrderApplicationFacade {
    private final OrderCommandHandler commandHandler;
    private final OrderQueryHandler queryHandler;

    public CreateOrderCommandResponse createOrder(CreateOrderCommand command) {
        return commandHandler.handle(command);
    }

    public UpdateOrderStatusCommandResponse updateOrderStatus(UpdateOrderStatusCommand command) {
        return commandHandler.handle(command);
    }

    public CancelOrderCommandResponse cancelOrder(CancelOrderCommand command) {
        return commandHandler.handle(command);
    }

    @Override
    public void deleteOrder(DeleteOrderCommand command) {
        commandHandler.handle(command);
    }

    @Override
    public OrderQueryResult getOrderById(GetOrderByIdQuery query) {
        return queryHandler.handle(query)
                .orElseThrow(() -> new OrderNotFoundIDException(query.orderID()));
    }

    @Override
    public OrderQueryDetailResult getOrderDetailById(GetOrderDetailByIdQuery query) {
        return queryHandler.handle(query)
                .orElseThrow(() -> new OrderNotFoundIDException(query.orderID()));
    }

    public PageResponse<OrderQueryResult> getOrdersByCustomer(GetOrdersByCustomerQuery query) {
        return queryHandler.handle(query);
    }

    public PageResponse<OrderQueryResult> getOrdersByCustomerAndStatus(GetOrdersByCustomerAndStatusQuery query) {
        return queryHandler.handle(query);
    }

    public PageResponse<OrderQueryResult> getOrdersByCustomerAndDateRange(GetOrdersByCustomerAndDateRangeQuery Query) {
        return queryHandler.handle(Query);
    }

    public OrderQueryDetailResult getOrderByCustomerAndId(GetOrderByCustomerAndIdQuery query) {
        return queryHandler.handle(query)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found for customer: " + query.customerId() + " and order: " + query.orderId()));
    }

    public List<OrderQueryResult> getRecentOrdersByCustomer(GetRecentOrdersByCustomerQuery query) {
        return queryHandler.handle(query);
    }
}