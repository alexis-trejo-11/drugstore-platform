package microservice.inventory_service.external.order.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.command.InsertOrderCommand;
import microservice.inventory_service.external.order.application.command.ReceiveOrderCommand;
import microservice.inventory_service.external.order.application.command.UpdateOrderStatusCommand;
import microservice.inventory_service.external.order.application.handler.command.CreateOrderCommandHandler;
import microservice.inventory_service.external.order.application.handler.command.ReceiveOrderCommandHandler;
import microservice.inventory_service.external.order.application.handler.command.UpdateOrderStatusCommandHandler;
import microservice.inventory_service.external.order.application.handler.queries.*;
import microservice.inventory_service.external.order.application.query.*;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;
import microservice.inventory_service.external.order.domain.port.input.OrderUseCase;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderUseCaseImpl implements OrderUseCase {
    private final CreateOrderCommandHandler createPOHandler;
    private final ReceiveOrderCommandHandler receivePOHandler;
    private final UpdateOrderStatusCommandHandler updateOrderStatusCommandHandler;

    private final GetOrdersByExpectedDateBeforeQueryHandler ordersByExpectedDateBeforeQueryHandler;
    private final GetOrderByIdQueryHandler orderByIdQueryHandler;
    private final GetOrderByNumberQueryHandler orderByNumberQueryHandler;
    private final GetOrdersByStatusQueryHandler ordersByStatusQueryHandler;
    private final GetOrderBySupplierIdQueryHandler orderBySupplierIdQueryHandler;

    @Override
    @Transactional
    public  OrderId insertOrder(InsertOrderCommand command) {
        return createPOHandler.handle(command);
    }

    @Override
    public void updateOrderStatus(UpdateOrderStatusCommand command) {
        updateOrderStatusCommandHandler.handle(command);
    }

    @Override
    @Transactional
    public void receiveOrder(ReceiveOrderCommand command) {
        receivePOHandler.handle(command);
    }


    @Override
    public Order getOrderById(GetOrderByIdQuery query) {
        return orderByIdQueryHandler.handle(query);
    }

    @Override
    public Order getOrderByNumber(GetOrderByNumberQuery query) {
        return orderByNumberQueryHandler.handle(query);
    }

    @Override
    public Page<Order> getOrdersByExpectedDateBefore(GetOrderByExpectedDateBeforeQuery query) {
        return ordersByExpectedDateBeforeQueryHandler.handle(query);
    }

    @Override
    public Page<Order> getOrdersByStatus(GetOrdersByStatusQuery query) {
        return ordersByStatusQueryHandler.handle(query);
    }

    @Override
    public Page<Order> getOrdersBySupplierId(GetOrdersBySupplierIdQuery query) {
        return orderBySupplierIdQueryHandler.handle(query);
    }
}
