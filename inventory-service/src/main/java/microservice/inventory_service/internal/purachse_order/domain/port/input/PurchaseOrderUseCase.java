package microservice.inventory_service.internal.purachse_order.domain.port.input;

import microservice.inventory_service.internal.purachse_order.application.command.InsertOrderCommand;
import microservice.inventory_service.internal.purachse_order.application.command.ReceiveOrderCommand;
import microservice.inventory_service.internal.purachse_order.application.command.UpdateOrderStatusCommand;
import microservice.inventory_service.internal.purachse_order.application.query.*;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrder;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.PurchaseOrderId;
import org.springframework.data.domain.Page;

public interface PurchaseOrderUseCase {
    PurchaseOrder getOrderById(GetOrderByIdQuery query);
    PurchaseOrder getOrderByNumber(GetOrderByNumberQuery query);
    Page<PurchaseOrder> getOrdersByExpectedDateBefore(GetOrderByExpectedDateBeforeQuery query);
    Page<PurchaseOrder> getOrdersByStatus(GetOrdersByStatusQuery query);
    Page<PurchaseOrder> getOrdersBySupplierId(GetOrdersBySupplierIdQuery query);


    PurchaseOrderId insertOrder(InsertOrderCommand command);
    void updateOrderStatus(UpdateOrderStatusCommand command);
    void receiveOrder(ReceiveOrderCommand command);

}
