package microservice.inventory_service.order.supplier_purchase.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.order.supplier_purchase.application.query.GetOrderByNumberQuery;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.exception.OrderNotFoundException;
import microservice.inventory_service.order.supplier_purchase.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrderByNumberQueryHandler {
    private final OrderRepository orderRepository;

    public PurchaseOrder handle(GetOrderByNumberQuery query) {
        return orderRepository.findByOrderNumber(query.orderNumber()).orElseThrow(() -> new OrderNotFoundException("PurchaseOrder not found"));
    }
}
