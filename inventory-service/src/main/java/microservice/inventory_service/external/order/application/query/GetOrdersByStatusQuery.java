package microservice.inventory_service.external.order.application.query;

import microservice.inventory_service.external.order.domain.entity.PurchaseOrderStatus;
import org.springframework.data.domain.Pageable;


public record GetOrdersByStatusQuery(PurchaseOrderStatus status, Pageable pageable) {
}
