package microservice.order_service.application.service;

import lombok.RequiredArgsConstructor;
import microservice.order_service.application.commands.handler.OrderQueryHandler;
import microservice.order_service.application.commands.request.CancelOrderCommand;
import microservice.order_service.application.commands.request.CreateOrderCommand;
import microservice.order_service.application.commands.request.UpdateOrderStatusCommand;
import microservice.order_service.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.application.commands.response.UpdateOrderStatusCommandResponse;
import microservice.order_service.application.commands.handler.OrderCommandHandler;
import microservice.order_service.application.exceptions.OrderNotFoundException;
import microservice.order_service.application.queries.request.*;
import microservice.order_service.application.queries.response.OrderQueryResponse;
import microservice.order_service.application.queries.response.OrderSummaryQueryResponse;
import microservice.order_service.application.queries.response.PagedOrderSummaryQueryResponse;
import microservice.order_service.domain.ports.input.OrderApplicationFacade;
import microservice.order_service.domain.ports.input.OrderCommandService;
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

    public PagedOrderSummaryQueryResponse getOrdersByCustomer(GetOrdersByCustomerQuery query) {
        return queryHandler.handle(query);
    }

    public PagedOrderSummaryQueryResponse getOrdersByCustomerAndStatus(GetOrdersByCustomerAndStatusQuery query) {
        return queryHandler.handle(query);
    }

    public PagedOrderSummaryQueryResponse getOrdersByCustomerAndDateRange(GetOrdersByCustomerAndDateRangeQuery Query) {
        return queryHandler.handle(Query);
    }

    public OrderQueryResponse getOrderByCustomerAndId(GetOrderByCustomerAndIdQuery query) {
        return queryHandler.handle(query)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found for customer: " + query.getCustomerId() + " and order: " + query.getOrderId()));
    }

    public List<OrderSummaryQueryResponse> getRecentOrdersByCustomer(GetRecentOrdersByCustomerQuery query) {
        return queryHandler.handle(query);
    }
}