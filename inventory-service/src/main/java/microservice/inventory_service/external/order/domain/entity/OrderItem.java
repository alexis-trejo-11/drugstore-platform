package microservice.inventory_service.external.order.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.ProductId;

import java.math.BigDecimal;
import java.util.Currency;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    private String id;
    private ProductId productId;
    private String productName;
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private Currency currency;
    private String batchNumber;


    public static OrderItem create(ProductId productId, String productName,
                                   Integer quantity, BigDecimal unitCost) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitCost.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit cost must be positive");
        }

        BigDecimal totalCost = unitCost.multiply(BigDecimal.valueOf(quantity));

        return new OrderItem(
                null,
                productId,
                productName,
                quantity,
                0,
                unitCost,
                totalCost,
                Currency.getInstance("USD"),
                null
        );
    }

    public void receiveQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Received quantity must be positive");
        }
        if (this.receivedQuantity + quantity > this.orderedQuantity) {
            throw new IllegalStateException("Cannot receive more than ordered quantity");
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

