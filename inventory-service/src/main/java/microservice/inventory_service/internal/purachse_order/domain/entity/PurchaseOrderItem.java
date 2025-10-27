package microservice.inventory_service.internal.purachse_order.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.internal.purachse_order.domain.exception.OrderItemValidationException;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.ProductId;

import java.math.BigDecimal;


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
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private String batchNumber;


    public static PurchaseOrderItem create(String itemId , ProductId productId, String productName,
                                           Integer quantity, BigDecimal unitCost) {
        if (quantity <= 0) {
            throw new OrderItemValidationException("Quantity must be positive");
        }
        if (unitCost.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderItemValidationException("Unit cost must be positive");
        }

        BigDecimal totalCost = unitCost.multiply(BigDecimal.valueOf(quantity));

        return new PurchaseOrderItem(
                itemId,
                productId,
                productName,
                quantity,
                0,
                unitCost,
                totalCost,
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

