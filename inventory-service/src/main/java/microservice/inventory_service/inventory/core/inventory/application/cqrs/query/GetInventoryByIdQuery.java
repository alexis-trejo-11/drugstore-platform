package microservice.inventory_service.inventory.core.inventory.application.cqrs.query;

import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;

public record GetInventoryByIdQuery(InventoryId inventoryId) {

    public static GetInventoryByIdQuery of(String inventoryId) {
        return new GetInventoryByIdQuery(InventoryId.of(inventoryId));
    }
}
