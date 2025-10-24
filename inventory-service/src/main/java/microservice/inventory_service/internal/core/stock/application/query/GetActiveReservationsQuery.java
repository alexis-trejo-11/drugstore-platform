package microservice.inventory_service.internal.core.stock.application.query;

import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;

public record GetActiveReservationsQuery(InventoryId inventoryId) {
}
