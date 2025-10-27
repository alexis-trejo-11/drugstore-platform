package microservice.inventory_service.internal.purachse_order.application.query;

import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.OrderStatus;
import org.springframework.data.domain.Pageable;


public record GetOrdersByStatusQuery(OrderStatus status, Pageable pageable) {
}
