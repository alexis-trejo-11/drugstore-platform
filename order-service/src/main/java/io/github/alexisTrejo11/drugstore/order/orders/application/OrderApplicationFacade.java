package io.github.alexisTrejo11.drugstore.order.orders.application;

import io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.*;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.status.*;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.request.*;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.CancelOrderOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.CreateOrderOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.UpdateOrderStatusOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderDetailResult;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderQueryResult;
import org.springframework.data.domain.Page;

public interface OrderApplicationFacade extends OrderCommandService, OrderQueryService {
}

interface OrderCommandService {
  CreateOrderOperationSummary createDeliveryOrder(CreateDeliveryOrderCommand command);

  CreateOrderOperationSummary createPickupOrder(CreatePickupOrderCommand command);

  void updateDeliveryAddress(UpdateOrderAddressCommand command);

  void updateDeliverMethod(UpdateOrderDeliverMethodCommand command);

  void deleteOrder(DeleteOrderCommand command);

  // Common Status Updates
  UpdateOrderStatusOperationSummary confirmOrder(ConfirmOrderCommand command);

  UpdateOrderStatusOperationSummary startPreparingOrder(PrepareOrderCommand command);

  UpdateOrderStatusOperationSummary completeOrder(CompleteOrderCommand command);

  // Shipping and Delivery
  UpdateOrderStatusOperationSummary shipOrder(ShipOrderCommand command);

  UpdateOrderStatusOperationSummary returnOrder(OrderDeliverFailCommand command);

  CancelOrderOperationSummary cancelOrder(CancelOrderCommand command);

  // Pickup and In-Store Orders
  UpdateOrderStatusOperationSummary readyForPickupOrder(OrderReadyToPickupCommand command);
}

interface OrderQueryService {
  OrderQueryResult getOrderByID(GetOrderByIDQuery query);

  OrderDetailResult getOrderByID(GetOrderDetailByIDQuery query);

  OrderDetailResult getOrderByIDAndUserID(GetOrderByIDAndUserIDQuery query);

  Page<OrderQueryResult> searchOrders(SearchOrdersQuery query);

  Page<OrderQueryResult> getOrdersByUserID(GetOrdersByUserIDQuery query);

  Page<OrderQueryResult> getOrdersByUserIDAndStatus(GetOrdersByUserIDAndStatusQuery query);

  Page<OrderQueryResult> getOrdersByUserIDAndDateRange(GetOrdersByUserIDAndDateRangeQuery Query);
}