package microservice.inventory_service.internal.purachse_order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.purachse_order.application.query.GetOrdersBySupplierIdQuery;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrder;
import microservice.inventory_service.internal.purachse_order.domain.port.output.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrderBySupplierIdQueryHandler {
    private final OrderRepository orderRepository;

    public Page<PurchaseOrder> handle(GetOrdersBySupplierIdQuery query) {
        return orderRepository.findBySupplierId(query.supplierId(), query.pageable());
    }
}
