package microservice.inventory_service.order.supplier_purchase.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.order.supplier_purchase.application.query.GetOrdersBySupplierIdQuery;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.port.output.PurchaseOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrderBySupplierIdQueryHandler {
    private final PurchaseOrderRepository orderRepository;

    public Page<PurchaseOrder> handle(GetOrdersBySupplierIdQuery query) {
        return orderRepository.findBySupplierId(query.supplierId(), query.pageable());
    }
}
