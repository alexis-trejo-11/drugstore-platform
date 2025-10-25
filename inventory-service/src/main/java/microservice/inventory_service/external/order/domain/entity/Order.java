package microservice.inventory_service.external.order.domain.entity;

import lombok.*;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderStatus;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private OrderId id;
    private String orderNumber;
    private String supplierId;
    private String supplierName;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String deliveryLocation;
    private UserId createdBy;
    private UserId approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItem> items;

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public static Order create(String supplierId,
                               String supplierName,
                               List<OrderItem> items,
                               LocalDateTime expectedDeliveryDate,
                               String deliveryLocation,
                               UserId createdBy) {


        BigDecimal totalAmount = BigDecimal.ZERO;
        if (items != null) {
            for (OrderItem item : items) {
                if (item.getUnitCost() == null || item.getOrderedQuantity() <= 0) {
                    throw new IllegalArgumentException("Invalid item unit cost or ordered quantity");
                }

                BigDecimal itemTotal = item.getUnitCost().multiply(BigDecimal.valueOf(item.getOrderedQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }

        Order order = new Order();
        order.supplierId = supplierId;
        order.supplierName = supplierName;
        order.items = items;
        order.expectedDeliveryDate = expectedDeliveryDate;
        order.deliveryLocation = deliveryLocation;
        order.createdBy = createdBy;
        order.status = OrderStatus.PENDING_APPROVAL;
        order.orderDate = LocalDateTime.now();
        order.createdAt = LocalDateTime.now();
        order.updatedAt = LocalDateTime.now();
        order.totalAmount = totalAmount;
        return order;
    }

    public void approve(UserId approvedBy) {
        if (this.status != OrderStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Can only approve orders pending approval");
        }
        this.status = OrderStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void sendToSupplier() {
        if (this.status != OrderStatus.APPROVED) {
            throw new IllegalStateException("Order must be approved before sending");
        }
        this.status = OrderStatus.SENT;
        this.updatedAt = LocalDateTime.now();
    }

    public void receiveItems(List<OrderItem> receivedItems, LocalDateTime deliveryDate) {
        if (this.status != OrderStatus.SENT && this.status != OrderStatus.PARTIALLY_RECEIVED) {
            throw new IllegalStateException("Invalid status for receiving items");
        }

        for (OrderItem receivedItem : receivedItems) {
            OrderItem orderItem = findItemById(receivedItem.getId());
            orderItem.receiveQuantity(receivedItem.getReceivedQuantity());
        }

        this.actualDeliveryDate = deliveryDate;
        updateStatusBasedOnReceivedItems();
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == OrderStatus.RECEIVED || this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order in current status");
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        if (this.status != OrderStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Can only reject orders pending approval");
        }
        this.status = OrderStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isFullyReceived() {
        return this.items.stream().allMatch(OrderItem::isFullyReceived);
    }

    public boolean isPartiallyReceived() {
        return this.items.stream().anyMatch(item -> item.getReceivedQuantity() > 0);
    }

    private void updateStatusBasedOnReceivedItems() {
        if (isFullyReceived()) {
            this.status = OrderStatus.RECEIVED;
        } else if (isPartiallyReceived()) {
            this.status = OrderStatus.PARTIALLY_RECEIVED;
        }
    }

    private OrderItem findItemById(String itemId) {
        return this.items.stream().filter(item -> Objects.equals(item.getId(), itemId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order item not found"));
    }

}