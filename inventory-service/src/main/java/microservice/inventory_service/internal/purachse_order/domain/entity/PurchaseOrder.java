package microservice.inventory_service.internal.purachse_order.domain.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.OrderStatus;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.UserId;

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
@Slf4j
public class PurchaseOrder {
    private PurchaseOrderId id;
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
    private LocalDateTime deletedAt;
    private List<PurchaseOrderItem> items;

    public List<PurchaseOrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public static PurchaseOrder create(
            PurchaseOrderId id,
            String supplierId,
            String supplierName,
            List<PurchaseOrderItem> items,
            LocalDateTime expectedDeliveryDate,
            String deliveryLocation,
            UserId createdBy,
            Currency currency
    ) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("PurchaseOrder must contain at least one item");
        }

        if (currency == null) {
            throw new IllegalArgumentException("Currency must be specified");
        }

        if (expectedDeliveryDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expected delivery date cannot be in the past");
        }

        if (id == null) {
            throw new IllegalArgumentException("PurchaseOrder ID is required of external entities");
        }

        BigDecimal totalAmount = getBigDecimal(items);

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.id = id;
        purchaseOrder.orderNumber = id.value();
        purchaseOrder.supplierId = supplierId;
        purchaseOrder.supplierName = supplierName;
        purchaseOrder.items = items;
        purchaseOrder.expectedDeliveryDate = expectedDeliveryDate;
        purchaseOrder.deliveryLocation = deliveryLocation;
        purchaseOrder.createdBy = createdBy;
        purchaseOrder.currency = currency;
        purchaseOrder.status = OrderStatus.PENDING_APPROVAL;
        purchaseOrder.orderDate = LocalDateTime.now();
        purchaseOrder.createdAt = LocalDateTime.now();
        purchaseOrder.updatedAt = LocalDateTime.now();
        purchaseOrder.totalAmount = totalAmount;
        return purchaseOrder;
    }

    private static BigDecimal getBigDecimal(List<PurchaseOrderItem> items) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (items != null) {
            for (PurchaseOrderItem item : items) {
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

    public void supplierSending() {
        if (this.status != OrderStatus.APPROVED) {
            throw new IllegalStateException("PurchaseOrder must be approved before sending");
        }
        this.status = OrderStatus.SENT;
        this.updatedAt = LocalDateTime.now();
    }

    public void receiveItems(List<PurchaseOrderItem> receivedItems, LocalDateTime deliveryDate) {
        if (this.status != OrderStatus.SENT && this.status != OrderStatus.PARTIALLY_RECEIVED) {
            throw new IllegalStateException("Invalid status for receiving items");
        }

        for (PurchaseOrderItem receivedItem : receivedItems) {
            PurchaseOrderItem purchaseOrderItem = findItemById(receivedItem.getId());
            purchaseOrderItem.receiveQuantity(receivedItem.getReceivedQuantity());
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
        return this.items.stream().allMatch(PurchaseOrderItem::isFullyReceived);
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

    private PurchaseOrderItem findItemById(String itemId) {
        return this.items.stream().filter(item -> Objects.equals(item.getId(), itemId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("PurchaseOrder item not found"));
    }

    public void validateHardDelete() {
        if (this.status != OrderStatus.CANCELLED && this.status != OrderStatus.REJECTED) {
            throw new IllegalStateException("Can only hard validateHardDelete orders that are cancelled or rejected");
        }
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();

    }

    public void validateUpdate() {
        log.info("Validating update for PurchaseOrder with status: {}", this.status);

        if (this.status != OrderStatus.DRAFT && this.status != OrderStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Cannot update order in current status");
        }

        log.info("PurchaseOrder update validated successfully.");
    }
}