package microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.query;

import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.InventoryId;

public record GetInventoryBatchesQuery(InventoryId inventoryId, Boolean activeOnly) {
}
