package microservice.inventory_service.external.order.domain.entity;

import lombok.*;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderStatus;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Currency;
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
    private Currency currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItem> items;

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public static Order create(
            OrderId id,
            String supplierId,
            String supplierName,
            List<OrderItem> items,
            LocalDateTime expectedDeliveryDate,
            String deliveryLocation,
            UserId createdBy,
            Currency currency
            ) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        if (currency == null) {
            throw new IllegalArgumentException("Currency must be specified");
        }

        if (expectedDeliveryDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expected delivery date cannot be in the past");
        }

        if (id == null) {
            throw new IllegalArgumentException("Order ID is required of external entities");
        }

        BigDecimal totalAmount = getBigDecimal(items);

        Order order = new Order();
        order.id = id;
        order.orderNumber = id.value();
        order.supplierId = supplierId;
        order.supplierName = supplierName;
        order.items = items;
        order.expectedDeliveryDate = expectedDeliveryDate;
        order.deliveryLocation = deliveryLocation;
        order.createdBy = createdBy;
        order.currency = currency;
        order.status = OrderStatus.PENDING_APPROVAL;
        order.orderDate = LocalDateTime.now();
        order.createdAt = LocalDateTime.now();
        order.updatedAt = LocalDateTime.now();
        order.totalAmount = totalAmount;
        return order;
    }

    private static BigDecimal getBigDecimal(List<OrderItem> items) {
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
        return totalAmount;
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