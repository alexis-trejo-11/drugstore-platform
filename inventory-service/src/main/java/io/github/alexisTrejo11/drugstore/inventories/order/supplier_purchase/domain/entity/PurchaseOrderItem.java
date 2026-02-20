package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity;

import lombok.*;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.exception.OrderItemValidationException;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.BaseDomainEntity;

import java.time.LocalDateTime;

@Getter
public class PurchaseOrderItem extends BaseDomainEntity<String> {
    private ProductId productId;
    private String productName;
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private String batchNumber;

    @Builder
    public PurchaseOrderItem(String id, LocalDateTime createdAt, LocalDateTime updatedAt,LocalDateTime deletedAt, Integer version, ProductId productId, String productName, Integer orderedQuantity, Integer receivedQuantity, String batchNumber) {
        super(id, createdAt, updatedAt, deletedAt, version);
        this.productId = productId;
        this.productName = productName;
        this.orderedQuantity = orderedQuantity;
        this.receivedQuantity = receivedQuantity;
        this.batchNumber = batchNumber;
    }

    public PurchaseOrderItem(String id, ProductId productId, String productName, Integer orderedQuantity, Integer receivedQuantity, String batchNumber) {
        super(id);
        this.productId = productId;
        this.productName = productName;
        this.orderedQuantity = orderedQuantity;
        this.receivedQuantity = receivedQuantity;
        this.batchNumber = batchNumber;
    }

    public boolean hasBeenReceived() {
        return receivedQuantity != null && receivedQuantity > 0;
    }

    public static PurchaseOrderItem create(String itemId , ProductId productId, String productName, Integer quantity) {
        return new PurchaseOrderItem(
                itemId,
                productId,
                productName,
                quantity,
                0,
                null
        );
    }

    public void receiveQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new OrderItemValidationException("Received quantity must be positive");
        }
        if (this.receivedQuantity + quantity > this.orderedQuantity) {
            throw new OrderItemValidationException("Cannot receive more than ordered quantity");
        }
        this.receivedQuantity += quantity;
    }

    public void assignBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
    public boolean isFullyReceived() {
        return this.receivedQuantity.equals(this.orderedQuantity);
    }
}

