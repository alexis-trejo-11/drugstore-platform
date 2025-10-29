package microservice.inventory_service.inventory.core.inventory.application.cqrs.query;

import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;

public record GetInventoryByProductQuery(ProductId productId) {
    public static GetInventoryByProductQuery of(String productId) {
        return new GetInventoryByProductQuery(ProductId.of(productId));
    }
}
