package microservice.inventory_service.external.order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.query.GetOrdersByStatusQuery;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import microservice.inventory_service.external.order.domain.port.output.PurchaseOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrdersByStatusQueryHandler {
    private final PurchaseOrderRepository purchaseRepository;

    public Page<PurchaseOrder> handle(GetOrdersByStatusQuery query) {
        return purchaseRepository.findByStatus(query.status(), query.pageable());
    }
}
