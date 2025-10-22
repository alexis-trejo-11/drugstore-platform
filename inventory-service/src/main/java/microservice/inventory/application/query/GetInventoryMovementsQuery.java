package microservice.inventory.application.query;

import microservice.inventory.domain.entity.valueobject.id.InventoryId;

import java.time.LocalDateTime;

public record GetInventoryMovementsQuery(InventoryId inventoryId, LocalDateTime startDate, LocalDateTime endDate) {
}
