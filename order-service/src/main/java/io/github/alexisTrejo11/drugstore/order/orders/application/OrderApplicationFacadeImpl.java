package io.github.alexisTrejo11.drugstore.order.orders.application;

import io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.*;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.status.*;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.request.*;
import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.order.orders.application.handler.command.OrderStatusCommandHandler;
import io.github.alexisTrejo11.drugstore.order.orders.application.handler.query.OrderQueryHandler;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.CancelOrderOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.CreateOrderOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.UpdateOrderStatusOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.application.handler.command.OrderCommandHandler;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderDetailResult;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderQueryResult;
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
    public CreateOrderOperationSummary createDeliveryOrder(CreateDeliveryOrderCommand command) {
        return commandHandler.handle(command);
    }

    @Override
    public CreateOrderOperationSummary createPickupOrder(CreatePickupOrderCommand command) {
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
    public CancelOrderOperationSummary cancelOrder(CancelOrderCommand command) {
        return statusCommandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusOperationSummary readyForPickupOrder(OrderReadyToPickupCommand command) {
        return statusCommandHandler.handle(command);
    }

    @Override
    public void deleteOrder(DeleteOrderCommand command) {
        commandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusOperationSummary confirmOrder(ConfirmOrderCommand command) {
        return statusCommandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusOperationSummary startPreparingOrder(PrepareOrderCommand command) {
        return statusCommandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusOperationSummary completeOrder(CompleteOrderCommand command) {
        return statusCommandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusOperationSummary shipOrder(ShipOrderCommand command) {
        return statusCommandHandler.handle(command);
    }

    @Override
    public UpdateOrderStatusOperationSummary returnOrder(OrderDeliverFailCommand command) {
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