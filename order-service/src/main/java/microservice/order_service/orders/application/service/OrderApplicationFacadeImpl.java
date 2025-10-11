package microservice.order_service.orders.application.service;

import lombok.RequiredArgsConstructor;
import microservice.order_service.orders.application.commands.handler.OrderStatusCommandHandler;
import microservice.order_service.orders.application.commands.request.DeleteOrderCommand;
import microservice.order_service.orders.application.commands.request.UpdateOrderAddressCommand;
import microservice.order_service.orders.application.commands.request.UpdateOrderDeliverMethodCommand;
import microservice.order_service.orders.application.commands.request.status.*;
import microservice.order_service.orders.application.exceptions.OrderNotFoundException;
import microservice.order_service.orders.application.queries.handler.OrderQueryHandler;
import microservice.order_service.orders.application.commands.request.CreateOrderCommand;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.UpdateOrderStatusCommandResult;
import microservice.order_service.orders.application.commands.handler.OrderCommandHandler;
import microservice.order_service.orders.application.exceptions.OrderNotFoundIDException;
import microservice.order_service.orders.application.queries.request.*;
import microservice.order_service.orders.application.queries.response.OrderDetailResult;
import microservice.order_service.orders.application.queries.response.OrderQueryResult;
import microservice.order_service.orders.domain.ports.input.OrderApplicationFacade;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderApplicationFacadeImpl implements OrderApplicationFacade {
    private final OrderCommandHandler commandHandler;
    private final OrderStatusCommandHandler statusCommandHandler;
    private final OrderQueryHandler queryHandler;

    // Commands
    @Override
    public CreateOrderCommandResponse createOrder(CreateOrderCommand command) {
        return commandHandler.handle(command);
    }

    @Override
    public void updateDeliveryAddress(UpdateOrderAddressCommand command) {
        commandHandler.handle(command);
    }

    @Override
    public void updateDeliverMethod(UpdateOrderDeliverMethodCommand command) {
        commandHandler.handle(command);
    }

    @Override
    public CancelOrderCommandResponse cancelOrder(CancelOrderCommand command) {
        return statusCommandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusCommandResult readyForPickupOrder(OrderReadyToPickupCommand command) {
        return statusCommandHandler.handle(command);
    }

    @Override
    public void deleteOrder(DeleteOrderCommand command) {
        commandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusCommandResult confirmOrder(ConfirmOrderCommand command) {
        return statusCommandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusCommandResult startPreparingOrder(PrepareOrderCommand command) {
        return statusCommandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusCommandResult completeOrder(CompleteOrderCommand command) {
        return statusCommandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusCommandResult shipOrder(ShipOrderCommand command) {
        return statusCommandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusCommandResult returnOrder(OrderDeliverFailCommand command) {
        return statusCommandHandler.handle(command);
    }


    // Queries
    @Override
    public OrderQueryResult getOrderByID(GetOrderByIDQuery query) {
        return queryHandler.handle(query);
    }

    @Override
    public OrderDetailResult getOrderByID(GetOrderDetailByIDQuery query) {
        return queryHandler.handle(query);
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
    public OrderDetailResult getOrderByIDAndUserID(GetOrderByIDAndUserIDQuery query) {
        return queryHandler.handle(query);
    }

    @Override
    public Page<OrderQueryResult> searchOrders(SearchOrdersQuery query) {
        return queryHandler.handle(query);
    }


}