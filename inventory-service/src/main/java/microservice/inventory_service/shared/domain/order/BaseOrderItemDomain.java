package microservice.inventory_service.shared.domain.order;

import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;

import java.time.LocalDateTime;

public abstract class BaseOrderItemDomain<T> extends BaseDomainEntity<T> {
    protected ProductId productId;
    protected String productName;

    public BaseOrderItemDomain(T id, LocalDateTime createdAt, LocalDateTime updatedAt, ProductId productId, String productName) {
        super(id, createdAt, updatedAt);
        this.productId = productId;
        this.productName = productName;
    }

    public BaseOrderItemDomain(T id, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, Integer version, ProductId productId, String productName) {
        super(id, createdAt, updatedAt, deletedAt, version);
        this.productId = productId;
        this.productName = productName;
    }

    public BaseOrderItemDomain(T id, ProductId productId, String productName) {
        super(id);
        this.productId = productId;
        this.productName = productName;
    }

    public ProductId getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }
}