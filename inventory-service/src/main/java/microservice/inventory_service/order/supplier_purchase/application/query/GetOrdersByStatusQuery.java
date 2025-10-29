package microservice.inventory_service.order.supplier_purchase.application.query;

import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.OrderStatus;
import org.springframework.data.domain.Pageable;


public record GetOrdersByStatusQuery(OrderStatus status, Pageable pageable) {
}
