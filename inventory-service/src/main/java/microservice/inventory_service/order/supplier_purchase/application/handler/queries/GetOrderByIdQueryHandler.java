package microservice.inventory_service.order.supplier_purchase.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.order.supplier_purchase.application.query.GetOrderByIdQuery;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.exception.OrderNotFoundException;
import microservice.inventory_service.order.supplier_purchase.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrderByIdQueryHandler {
    private final OrderRepository orderRepository;

    public PurchaseOrder handle(GetOrderByIdQuery query) {
        return orderRepository.findById(query.purchaseOrderId()).orElseThrow(() -> new OrderNotFoundException("PurchaseOrder not found"));
    }
}
