package microservice.inventory_service.external.order.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.handler.queries.GetOrderByIdQueryHandler;
import microservice.inventory_service.external.order.application.handler.queries.GetOrderByNumberQueryHandler;
import microservice.inventory_service.external.order.application.handler.queries.GetOrdersByExpectedDateBeforeQueryHandler;
import microservice.inventory_service.external.order.application.handler.queries.GetOrdersByStatusQueryHandler;
import microservice.inventory_service.external.order.application.query.GetOrderByExpectedDateBeforeQuery;
import microservice.inventory_service.external.order.application.query.GetOrderByIdQuery;
import microservice.inventory_service.external.order.application.query.GetOrderByNumberQuery;
import microservice.inventory_service.external.order.application.query.GetOrdersByStatusQuery;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import microservice.inventory_service.external.order.domain.port.input.OrderQueryService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderQueryServiceImpl implements OrderQueryService {
    private final GetOrdersByExpectedDateBeforeQueryHandler ordersByExpectedDateBeforeQueryHandler;
    private final GetOrderByIdQueryHandler orderByIdQueryHandler;
    private final GetOrderByNumberQueryHandler orderByNumberQueryHandler;
    private final GetOrdersByStatusQueryHandler ordersByStatusQueryHandler;

    public PurchaseOrder getOrderById(GetOrderByIdQuery query) {
        return orderByIdQueryHandler.handle(query);
    }

    public PurchaseOrder getOrderByNumber(GetOrderByNumberQuery query) {
        return orderByNumberQueryHandler.handle(query);
    }

    public Page<PurchaseOrder> getOrdersByExpectedDateBefore(GetOrderByExpectedDateBeforeQuery query) {
        return ordersByExpectedDateBeforeQueryHandler.handle(query);
    }

    public Page<PurchaseOrder> getOrdersByStatus(GetOrdersByStatusQuery query) {
        return ordersByStatusQueryHandler.handle(query);
    }

}
