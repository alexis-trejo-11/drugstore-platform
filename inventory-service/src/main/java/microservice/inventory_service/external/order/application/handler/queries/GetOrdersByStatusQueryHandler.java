package microservice.inventory_service.external.order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.query.GetOrdersByStatusQuery;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.port.output.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrdersByStatusQueryHandler {
    private final OrderRepository orderRepository;

    public Page<Order> handle(GetOrdersByStatusQuery query) {
        return orderRepository.findByStatus(query.status(), query.pageable());
    }
}
