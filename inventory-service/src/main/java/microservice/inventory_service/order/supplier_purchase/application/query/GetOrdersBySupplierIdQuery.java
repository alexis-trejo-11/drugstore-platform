package microservice.inventory_service.order.supplier_purchase.application.query;

import org.springframework.data.domain.Pageable;

public record GetOrdersBySupplierIdQuery(String supplierId, Pageable pageable) {
}
