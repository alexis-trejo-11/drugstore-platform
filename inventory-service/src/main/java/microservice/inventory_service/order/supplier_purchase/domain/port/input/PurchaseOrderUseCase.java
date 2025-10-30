package microservice.inventory_service.order.supplier_purchase.domain.port.input;

import microservice.inventory_service.order.supplier_purchase.application.command.InsertOrderCommand;
import microservice.inventory_service.order.supplier_purchase.application.command.ReceiveOrderCommand;
import microservice.inventory_service.order.supplier_purchase.application.command.UpdatePurchaseOrderStatusCommand;
import microservice.inventory_service.order.supplier_purchase.application.query.*;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import org.springframework.data.domain.Page;

public interface PurchaseOrderUseCase {
    PurchaseOrder getOrderById(GetOrderByIdQuery query);
    Page<PurchaseOrder> getOrdersByExpectedDateBefore(GetOrderByExpectedDateBeforeQuery query);
    Page<PurchaseOrder> getOrdersByStatus(GetOrdersByStatusQuery query);
    Page<PurchaseOrder> getOrdersBySupplierId(GetOrdersBySupplierIdQuery query);


    PurchaseOrderId insertOrder(InsertOrderCommand command);
    void updatePurchaseOrderStatus(UpdatePurchaseOrderStatusCommand command);
    void receiveOrder(ReceiveOrderCommand command);

}
