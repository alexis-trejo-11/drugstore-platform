package microservice.inventory_service.internal.core.inventory.application.cqrs.query;

import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.ProductId;

public record GetInventoryByProductQuery(ProductId productId) {
    public static GetInventoryByProductQuery of(String productId) {
        return new GetInventoryByProductQuery(ProductId.of(productId));
    }
}
