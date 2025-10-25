package microservice.inventory_service.external.order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.query.GetOrderByExpectedDateBeforeQuery;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.port.output.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrdersByExpectedDateBeforeQueryHandler {
    private final OrderRepository orderRepository;

    public Page<Order> handle(GetOrderByExpectedDateBeforeQuery query) {
        return orderRepository.findByExpectedDeliveryDateBefore(query.date(), query.pageable());
    }
}
