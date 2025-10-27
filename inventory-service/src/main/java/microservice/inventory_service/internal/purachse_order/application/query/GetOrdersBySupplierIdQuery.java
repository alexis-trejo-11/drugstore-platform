package microservice.inventory_service.internal.purachse_order.application.query;

import org.springframework.data.domain.Pageable;

public record GetOrdersBySupplierIdQuery(String supplierId, Pageable pageable) {
}
