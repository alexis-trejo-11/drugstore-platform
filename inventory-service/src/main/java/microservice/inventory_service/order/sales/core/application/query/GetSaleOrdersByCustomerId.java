package microservice.inventory_service.order.sales.core.application.query;

import org.springframework.data.domain.Pageable;

public record GetSaleOrdersByCustomerId(
        String customerId,
        Pageable pageable
) {
}
