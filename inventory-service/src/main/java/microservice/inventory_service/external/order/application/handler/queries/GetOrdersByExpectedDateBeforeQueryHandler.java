package microservice.inventory_service.external.order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.query.GetOrderByExpectedDateBeforeQuery;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import microservice.inventory_service.external.order.domain.port.output.PurchaseOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrdersByExpectedDateBeforeQueryHandler {
    private final PurchaseOrderRepository purchaseRepository;

    public Page<PurchaseOrder> handle(GetOrderByExpectedDateBeforeQuery query) {
        return purchaseRepository.findByExpectedDeliveryDateBefore(query.date(), query.pageable());
    }
}
