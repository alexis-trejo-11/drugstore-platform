package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application;

import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.handler.queries.GetOrderByIdQueryHandler;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.handler.queries.GetOrderBySupplierIdQueryHandler;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.handler.queries.GetOrdersByExpectedDateBeforeQueryHandler;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.handler.queries.GetOrdersByStatusQueryHandler;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query.GetOrderByExpectedDateBeforeQuery;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query.GetOrderByIdQuery;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query.GetOrdersByStatusQuery;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query.GetOrdersBySupplierIdQuery;
import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command.InitOrderCommand;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command.ReceiveOrderCommand;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command.UpdatePurchaseOrderStatusCommand;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.handler.command.CreatePurchaseOrderCmdHandler;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.handler.command.FulFillPurchaseOrderCmdHandler;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.handler.command.ReceiveOrderCmdHandler;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.handler.command.UpdatePurchaseOrderStatusCmdHandler;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.handler.queries.*;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query.*;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.PurchaseOrder;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.port.input.PurchaseOrderUseCase;
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
