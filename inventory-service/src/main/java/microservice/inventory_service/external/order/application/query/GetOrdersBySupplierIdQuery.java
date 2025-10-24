package microservice.inventory_service.external.order.application.query;

import org.springframework.data.domain.Pageable;

public record GetOrdersBySupplierIdQuery(String supplierId, Pageable pageable) {
}
