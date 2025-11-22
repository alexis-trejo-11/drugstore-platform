package microservice.inventory_service.order.supplier_purchase.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.order.supplier_purchase.application.query.GetOrdersByStatusQuery;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.port.output.PurchaseOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrdersByStatusQueryHandler {
    private final PurchaseOrderRepository orderRepository;

    public Page<PurchaseOrder> handle(GetOrdersByStatusQuery query) {
        return orderRepository.findByStatus(query.status(), query.pageable());
    }
}
