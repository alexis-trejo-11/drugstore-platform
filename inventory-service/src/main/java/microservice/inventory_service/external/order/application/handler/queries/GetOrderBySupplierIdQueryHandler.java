package microservice.inventory_service.external.order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.query.GetOrdersBySupplierIdQuery;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.port.output.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrderBySupplierIdQueryHandler {
    private final OrderRepository orderRepository;

    public Page<Order> handle(GetOrdersBySupplierIdQuery query) {
        return orderRepository.findBySupplierId(query.supplierId(), query.pageable());
    }
}
