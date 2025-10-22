package microservice.inventory.application.query;

import microservice.inventory.domain.entity.valueobject.id.InventoryId;

public record GetInventoryBatchesQuery(InventoryId inventoryId, Boolean activeOnly) {
}
