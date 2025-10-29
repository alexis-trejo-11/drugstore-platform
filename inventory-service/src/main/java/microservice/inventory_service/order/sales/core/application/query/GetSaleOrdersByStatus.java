package microservice.inventory_service.order.sales.core.application.query;

import microservice.inventory_service.shared.domain.order.OrderStatus;
import org.springframework.data.domain.Pageable;

public record GetSaleOrdersByStatus(
        OrderStatus status,
        Pageable pageable
) {
}
