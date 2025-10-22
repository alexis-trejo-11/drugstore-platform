package microservice.inventory.application.query;

import microservice.inventory.domain.entity.valueobject.id.InventoryId;

public record GetInventoryByIdQuery(InventoryId inventoryId) {
}
