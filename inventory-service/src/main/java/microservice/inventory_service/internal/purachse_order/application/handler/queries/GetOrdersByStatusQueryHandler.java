package microservice.inventory_service.internal.purachse_order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.purachse_order.application.query.GetOrdersByStatusQuery;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrder;
import microservice.inventory_service.internal.purachse_order.domain.port.output.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrdersByStatusQueryHandler {
    private final OrderRepository orderRepository;

    public Page<PurchaseOrder> handle(GetOrdersByStatusQuery query) {
        return orderRepository.findByStatus(query.status(), query.pageable());
    }
}
