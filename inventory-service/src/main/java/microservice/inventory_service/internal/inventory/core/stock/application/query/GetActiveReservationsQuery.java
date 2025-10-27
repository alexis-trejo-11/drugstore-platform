package microservice.inventory_service.internal.inventory.core.stock.application.query;

import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.InventoryId;

public record GetActiveReservationsQuery(InventoryId inventoryId) {
}
