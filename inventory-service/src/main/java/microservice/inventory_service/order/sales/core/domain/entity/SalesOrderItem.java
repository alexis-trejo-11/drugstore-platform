package microservice.inventory_service.order.sales.core.domain.entity;

import microservice.inventory_service.shared.domain.order.BaseOrderItemDomain;

import java.time.LocalDateTime;

public class SalesOrderItem extends BaseOrderItemDomain<String> {
    public Integer orderedQuantity;
    public Integer receivedQuantity;

    public SalesOrderItem(String id, LocalDateTime createdAt, LocalDateTime updatedAt, String productId, String productName, Integer orderedQuantity, Integer receivedQuantity) {
        super(id, createdAt, updatedAt, productId, productName);
        this.orderedQuantity = orderedQuantity;
        this.receivedQuantity = receivedQuantity;
    }

    public SalesOrderItem(String id, String productId, String productName, Integer orderedQuantity, Integer receivedQuantity) {
        super(id, productId, productName);
        this.orderedQuantity = orderedQuantity;
        this.receivedQuantity = receivedQuantity;
    }

    public Integer getOrderedQuantity() {
        return orderedQuantity;
    }

    public Integer getReceivedQuantity() {
        return receivedQuantity;
    }
}
