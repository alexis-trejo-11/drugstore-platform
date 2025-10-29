package microservice.inventory_service.shared.domain.order;

import java.time.LocalDateTime;

public abstract class BaseOrderItemDomain<T> extends BaseDomainEntity<T> {
    protected String productId;
    protected String productName;

    public BaseOrderItemDomain(T id, LocalDateTime createdAt, LocalDateTime updatedAt, String productId, String productName) {
        super(id, createdAt, updatedAt);
        this.productId = productId;
        this.productName = productName;
    }

    public BaseOrderItemDomain(T id, String productId, String productName) {
        super(id);
        this.productId = productId;
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }
}