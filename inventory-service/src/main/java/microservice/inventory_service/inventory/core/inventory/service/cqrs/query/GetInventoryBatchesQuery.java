package microservice.inventory_service.inventory.core.inventory.service.cqrs.query;

import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import org.springframework.data.domain.Pageable;

public record GetInventoryBatchesQuery(InventoryId inventoryId, Boolean activeOnly, Pageable pageable) {
}
