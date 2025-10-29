package microservice.inventory_service.order.sales.core.domain.port;

import microservice.inventory_service.order.sales.core.application.command.*;
import microservice.inventory_service.order.sales.core.application.query.GetSaleOrdersByCustomerId;
import microservice.inventory_service.order.sales.core.application.query.GetSaleOrdersById;
import microservice.inventory_service.order.sales.core.application.query.GetSaleOrdersByStatus;
import microservice.inventory_service.order.sales.core.domain.entity.SaleOrder;
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
