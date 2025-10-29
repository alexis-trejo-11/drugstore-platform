package microservice.inventory_service.order.supplier_purchase.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.order.supplier_purchase.domain.exception.OrderItemValidationException;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseOrderItem {
    private String id;
    private ProductId productId;
    private String productName;
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private String batchNumber;

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

    public Integer getRemainingQuantity() {
        return this.orderedQuantity - this.receivedQuantity;
    }

}

