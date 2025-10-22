package microservice.inventory_service.inventory.application.query;

import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;

public record GetInventoryByIdQuery(InventoryId inventoryId) {
}
