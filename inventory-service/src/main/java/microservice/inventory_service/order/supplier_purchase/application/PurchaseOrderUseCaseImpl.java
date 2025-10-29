package microservice.inventory_service.order.supplier_purchase.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.order.supplier_purchase.application.command.InsertOrderCommand;
import microservice.inventory_service.order.supplier_purchase.application.command.ReceiveOrderCommand;
import microservice.inventory_service.order.supplier_purchase.application.command.UpdateOrderStatusCommand;
import microservice.inventory_service.order.supplier_purchase.application.handler.command.CreatePurchaseOrderCommandHandler;
import microservice.inventory_service.order.supplier_purchase.application.handler.command.ReceiveOrderCommandHandler;
import microservice.inventory_service.order.supplier_purchase.application.handler.command.UpdateOrderStatusCommandHandler;
import microservice.inventory_service.order.supplier_purchase.application.handler.queries.*;
import microservice.inventory_service.order.supplier_purchase.application.query.*;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.order.supplier_purchase.domain.port.input.PurchaseOrderUseCase;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PurchaseOrderUseCaseImpl implements PurchaseOrderUseCase {
    private final CreatePurchaseOrderCommandHandler createPOHandler;
    private final ReceiveOrderCommandHandler receivePOHandler;
    private final UpdateOrderStatusCommandHandler updateOrderStatusCommandHandler;

    private final GetOrdersByExpectedDateBeforeQueryHandler ordersByExpectedDateBeforeQueryHandler;
    private final GetOrderByIdQueryHandler orderByIdQueryHandler;
    private final GetOrderByNumberQueryHandler orderByNumberQueryHandler;
    private final GetOrdersByStatusQueryHandler ordersByStatusQueryHandler;
    private final GetOrderBySupplierIdQueryHandler orderBySupplierIdQueryHandler;

    @Override
    @Transactional
    public PurchaseOrderId insertOrder(InsertOrderCommand command) {
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
    public PurchaseOrder getOrderById(GetOrderByIdQuery query) {
        return orderByIdQueryHandler.handle(query);
    }

    @Override
    public PurchaseOrder getOrderByNumber(GetOrderByNumberQuery query) {
        return orderByNumberQueryHandler.handle(query);
    }

    @Override
    public Page<PurchaseOrder> getOrdersByExpectedDateBefore(GetOrderByExpectedDateBeforeQuery query) {
        return ordersByExpectedDateBeforeQueryHandler.handle(query);
    }

    @Override
    public Page<PurchaseOrder> getOrdersByStatus(GetOrdersByStatusQuery query) {
        return ordersByStatusQueryHandler.handle(query);
    }

    @Override
    public Page<PurchaseOrder> getOrdersBySupplierId(GetOrdersBySupplierIdQuery query) {
        return orderBySupplierIdQueryHandler.handle(query);
    }
}
