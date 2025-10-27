package microservice.inventory_service.internal.purachse_order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.purachse_order.application.query.GetOrderByNumberQuery;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrder;
import microservice.inventory_service.internal.purachse_order.domain.exception.OrderNotFoundException;
import microservice.inventory_service.internal.purachse_order.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrderByNumberQueryHandler {
    private final OrderRepository orderRepository;

    public PurchaseOrder handle(GetOrderByNumberQuery query) {
        return orderRepository.findByOrderNumber(query.orderNumber()).orElseThrow(() -> new OrderNotFoundException("PurchaseOrder not found"));
    }
}
