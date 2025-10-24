package microservice.inventory_service.external.order.domain.port.input;

import microservice.inventory_service.external.order.application.query.GetOrderByExpectedDateBeforeQuery;
import microservice.inventory_service.external.order.application.query.GetOrderByIdQuery;
import microservice.inventory_service.external.order.application.query.GetOrderByNumberQuery;
import microservice.inventory_service.external.order.application.query.GetOrdersByStatusQuery;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import org.springframework.data.domain.Page;

public interface OrderQueryService {
    PurchaseOrder getOrderById(GetOrderByIdQuery query);
    PurchaseOrder getOrderByNumber(GetOrderByNumberQuery query);
    Page<PurchaseOrder> getOrdersByExpectedDateBefore(GetOrderByExpectedDateBeforeQuery query);
    Page<PurchaseOrder> getOrdersByStatus(GetOrdersByStatusQuery query);

}
