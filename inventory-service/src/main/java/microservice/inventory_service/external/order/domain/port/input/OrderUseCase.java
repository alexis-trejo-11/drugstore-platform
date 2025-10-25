package microservice.inventory_service.external.order.domain.port.input;

import microservice.inventory_service.external.order.application.command.InsertOrderCommand;
import microservice.inventory_service.external.order.application.command.ReceiveOrderCommand;
import microservice.inventory_service.external.order.application.command.UpdateOrderStatusCommand;
import microservice.inventory_service.external.order.application.query.*;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;
import org.springframework.data.domain.Page;

public interface OrderUseCase {
    Order getOrderById(GetOrderByIdQuery query);
    Order getOrderByNumber(GetOrderByNumberQuery query);
    Page<Order> getOrdersByExpectedDateBefore(GetOrderByExpectedDateBeforeQuery query);
    Page<Order> getOrdersByStatus(GetOrdersByStatusQuery query);
    Page<Order> getOrdersBySupplierId(GetOrdersBySupplierIdQuery query);


    OrderId insertOrder(InsertOrderCommand command);
    void updateOrderStatus(UpdateOrderStatusCommand command);
    void receiveOrder(ReceiveOrderCommand command);

}
