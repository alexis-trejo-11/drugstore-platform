package microservice.inventory_service.external.order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.query.GetOrderByNumberQuery;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import microservice.inventory_service.external.order.domain.exception.PurchaseOrderNotFoundException;
import microservice.inventory_service.external.order.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrderByNumberQueryHandler {
    private final PurchaseOrderRepository purchaseRepository;

    public PurchaseOrder handle(GetOrderByNumberQuery query) {
        return purchaseRepository.findByOrderNumber(query.orderNumber()).orElseThrow(() -> new PurchaseOrderNotFoundException("Purchase order not found"));
    }
}
