package microservice.inventory_service.inventory.core.stock.application.query;

import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;

public record GetActiveReservationsQuery(InventoryId inventoryId) {
}
