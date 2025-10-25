package microservice.inventory_service.external.order.application.handler.queries;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.query.GetOrderByNumberQuery;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.exception.OrderNotFoundException;
import microservice.inventory_service.external.order.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrderByNumberQueryHandler {
    private final OrderRepository orderRepository;

    public Order handle(GetOrderByNumberQuery query) {
        return orderRepository.findByOrderNumber(query.orderNumber()).orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }
}
