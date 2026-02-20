package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.port;

import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.command.*;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.command.*;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.query.GetSaleOrdersByCustomerId;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.query.GetSaleOrdersById;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.query.GetSaleOrdersByStatus;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.SaleOrder;
import org.springframework.data.domain.Page;

public interface SalesOrderUseCase {
  Page<SaleOrder> getSalesOrdersByCustomerUserId(GetSaleOrdersByCustomerId query);

  Page<SaleOrder> getSalesOrdersByStatus(GetSaleOrdersByStatus query);

  SaleOrder getSalesOrderById(GetSaleOrdersById query);

  void receiveSaleOrder(ReceiveOrderSaleCommand command);

  void confirmPayment(ConfirmSaleOrderCommand command);

  void fulfillOrder(FullFillOrderCommand command);

  void readyToDelivery(SendSaleOrderToDeliveryCommand command);

  void cancelOrder(CancelSaleOrderCommand command);

  void delete(DeleteSaleOrderCommand command);

}
