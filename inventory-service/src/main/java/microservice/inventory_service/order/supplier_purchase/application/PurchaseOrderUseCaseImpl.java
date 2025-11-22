package microservice.inventory_service.order.supplier_purchase.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.order.supplier_purchase.application.command.InitOrderCommand;
import microservice.inventory_service.order.supplier_purchase.application.command.ReceiveOrderCommand;
import microservice.inventory_service.order.supplier_purchase.application.command.UpdatePurchaseOrderStatusCommand;
import microservice.inventory_service.order.supplier_purchase.application.handler.command.CreatePurchaseOrderCmdHandler;
import microservice.inventory_service.order.supplier_purchase.application.handler.command.FulFillPurchaseOrderCmdHandler;
import microservice.inventory_service.order.supplier_purchase.application.handler.command.ReceiveOrderCmdHandler;
import microservice.inventory_service.order.supplier_purchase.application.handler.command.UpdatePurchaseOrderStatusCmdHandler;
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
    private final CreatePurchaseOrderCmdHandler createPOHandler;
    private final ReceiveOrderCmdHandler receivePOHandler;
    private final UpdatePurchaseOrderStatusCmdHandler updatePOStatusHandler;
    private final FulFillPurchaseOrderCmdHandler fullFillOrderCommand;

    private final GetOrdersByExpectedDateBeforeQueryHandler ordersByExpectedDateBeforeQuery;
    private final GetOrderByIdQueryHandler orderByIdQuery;
    private final GetOrdersByStatusQueryHandler ordersByStatusQuery;
    private final GetOrderBySupplierIdQueryHandler orderBySupplierIdQuery;

    @Override
    @Transactional
    public PurchaseOrderId initOrder(InitOrderCommand command) {
        return createPOHandler.handle(command);
    }

    @Override
    public void fullFillOrder(ReceiveOrderCommand command) {
        fullFillOrderCommand.handle(command);
    }

    @Override
    public void updatePurchaseOrderStatus(UpdatePurchaseOrderStatusCommand command) {
        updatePOStatusHandler.handle(command);
    }

    @Override
    public void receiveOrder(ReceiveOrderCommand command) {
        receivePOHandler.handle(command);
    }

    @Override
    public PurchaseOrder getOrderById(GetOrderByIdQuery query) {
        return orderByIdQuery.handle(query);
    }


    @Override
    public Page<PurchaseOrder> getOrdersByExpectedDateBefore(GetOrderByExpectedDateBeforeQuery query) {
        return ordersByExpectedDateBeforeQuery.handle(query);
    }

    @Override
    public Page<PurchaseOrder> getOrdersByStatus(GetOrdersByStatusQuery query) {
        return ordersByStatusQuery.handle(query);
    }

    @Override
    public Page<PurchaseOrder> getOrdersBySupplierId(GetOrdersBySupplierIdQuery query) {
        return orderBySupplierIdQuery.handle(query);
    }
}
