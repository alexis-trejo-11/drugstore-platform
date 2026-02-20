package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.port.input;

import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command.InitOrderCommand;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command.ReceiveOrderCommand;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command.UpdatePurchaseOrderStatusCommand;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query.GetOrderByExpectedDateBeforeQuery;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query.GetOrderByIdQuery;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query.GetOrdersByStatusQuery;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query.GetOrdersBySupplierIdQuery;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query.*;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.PurchaseOrder;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import org.springframework.data.domain.Page;

public interface PurchaseOrderUseCase {
  PurchaseOrder getOrderById(GetOrderByIdQuery query);

  Page<PurchaseOrder> getOrdersByExpectedDateBefore(GetOrderByExpectedDateBeforeQuery query);

  Page<PurchaseOrder> getOrdersByStatus(GetOrdersByStatusQuery query);

  Page<PurchaseOrder> getOrdersBySupplierId(GetOrdersBySupplierIdQuery query);

  PurchaseOrderId initOrder(InitOrderCommand command);

  void fullFillOrder(ReceiveOrderCommand command);

  void updatePurchaseOrderStatus(UpdatePurchaseOrderStatusCommand command);

  void receiveOrder(ReceiveOrderCommand command);

}
