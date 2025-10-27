package microservice.inventory_service.internal.inventory.core.stock.application.query;

import org.springframework.data.domain.Pageable;

public record GetLowStockInventoriesQuery(Pageable pagination) {
}
