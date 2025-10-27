package microservice.inventory_service.internal.purachse_order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.purachse_order.application.query.GetOrderByExpectedDateBeforeQuery;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrder;
import microservice.inventory_service.internal.purachse_order.domain.port.output.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrdersByExpectedDateBeforeQueryHandler {
    private final OrderRepository orderRepository;

    public Page<PurchaseOrder> handle(GetOrderByExpectedDateBeforeQuery query) {
        return orderRepository.findByExpectedDeliveryDateBefore(query.date(), query.pageable());
    }
}
