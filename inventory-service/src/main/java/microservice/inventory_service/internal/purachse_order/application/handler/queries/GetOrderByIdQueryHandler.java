package microservice.inventory_service.internal.purachse_order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.purachse_order.application.query.GetOrderByIdQuery;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrder;
import microservice.inventory_service.internal.purachse_order.domain.exception.OrderNotFoundException;
import microservice.inventory_service.internal.purachse_order.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrderByIdQueryHandler {
    private final OrderRepository orderRepository;

    public PurchaseOrder handle(GetOrderByIdQuery query) {
        return orderRepository.findById(query.purchaseOrderId()).orElseThrow(() -> new OrderNotFoundException("PurchaseOrder not found"));
    }
}
