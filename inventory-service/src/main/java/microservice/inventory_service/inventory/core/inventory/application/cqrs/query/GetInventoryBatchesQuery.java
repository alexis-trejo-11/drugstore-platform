package microservice.inventory_service.inventory.core.inventory.application.cqrs.query;

import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;

public record GetInventoryBatchesQuery(InventoryId inventoryId, Boolean activeOnly) {
}
