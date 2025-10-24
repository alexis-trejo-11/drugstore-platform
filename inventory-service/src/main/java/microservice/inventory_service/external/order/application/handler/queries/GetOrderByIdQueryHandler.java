package microservice.inventory_service.external.order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.query.GetOrderByIdQuery;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import microservice.inventory_service.external.order.domain.exception.PurchaseOrderNotFoundException;
import microservice.inventory_service.external.order.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrderByIdQueryHandler {
    private final PurchaseOrderRepository purchaseRepository;

    public PurchaseOrder handle(GetOrderByIdQuery query) {
        return purchaseRepository.findById(query.purchaseOrderId()).orElseThrow(() -> new PurchaseOrderNotFoundException("Purchase order not found"));
    }
}
