package microservice.purchase.domain.entity;

import lombok.Getter;
import microservice.inventory.domain.entity.valueobject.id.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class PurchaseOrder {
    private PurchaseOrderId id;
    private String orderNumber;
    private String supplierId;
    private String supplierName;
    private List<PurchaseOrderItem> items;
    private BigDecimal totalAmount;
    private PurchaseOrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String deliveryLocation;
    private UserId createdBy;
    private UserId approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private PurchaseOrder(PurchaseOrderId id, String orderNumber, String supplierId, String supplierName,
                          List<PurchaseOrderItem> items, BigDecimal totalAmount,
                          PurchaseOrderStatus status, LocalDateTime orderDate,
                          LocalDateTime expectedDeliveryDate, LocalDateTime actualDeliveryDate,
                          String deliveryLocation, UserId createdBy, UserId approvedBy,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.actualDeliveryDate = actualDeliveryDate;
        this.deliveryLocation = deliveryLocation;
        this.createdBy = createdBy;
        this.approvedBy = approvedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public List<PurchaseOrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void approve(UserId approvedBy) {
        if (this.status != PurchaseOrderStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Can only approve orders pending approval");
        }
        this.status = PurchaseOrderStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void sendToSupplier() {
        if (this.status != PurchaseOrderStatus.APPROVED) {
            throw new IllegalStateException("Order must be approved before sending");
        }
        this.status = PurchaseOrderStatus.SENT;
        this.updatedAt = LocalDateTime.now();
    }

    public void receiveItems(List<PurchaseOrderItem> receivedItems, LocalDateTime deliveryDate) {
        if (this.status != PurchaseOrderStatus.SENT && this.status != PurchaseOrderStatus.PARTIALLY_RECEIVED) {
            throw new IllegalStateException("Invalid status for receiving items");
        }

        for (PurchaseOrderItem receivedItem : receivedItems) {
            PurchaseOrderItem orderItem = findItemById(receivedItem.getId());
            orderItem.receiveQuantity(receivedItem.getReceivedQuantity());
        }

        this.actualDeliveryDate = deliveryDate;
        updateStatusBasedOnReceivedItems();
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == PurchaseOrderStatus.RECEIVED || this.status == PurchaseOrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order in current status");
        }
        this.status = PurchaseOrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        if (this.status != PurchaseOrderStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Can only reject orders pending approval");
        }
        this.status = PurchaseOrderStatus.REJECTED;
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
            this.status = PurchaseOrderStatus.RECEIVED;
        } else if (isPartiallyReceived()) {
            this.status = PurchaseOrderStatus.PARTIALLY_RECEIVED;
        }
    }

    private PurchaseOrderItem findItemById(int itemId) {
        return this.items.stream().filter(item -> item.getId() == itemId).findFirst().orElseThrow(() -> new IllegalArgumentException("Order item not found"));
    }

    public static PurchaseOrderReconstructor reconstructor() {
        return new PurchaseOrderReconstructor();
    }

    public static class PurchaseOrderReconstructor {
        private PurchaseOrderId id;
        private String orderNumber;
        private String supplierId;
        private String supplierName;
        private List<PurchaseOrderItem> items;
        private BigDecimal totalAmount;
        private PurchaseOrderStatus status;
        private LocalDateTime orderDate;
        private LocalDateTime expectedDeliveryDate;
        private LocalDateTime actualDeliveryDate;
        private String deliveryLocation;
        private UserId createdBy;
        private UserId approvedBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public PurchaseOrderReconstructor id(PurchaseOrderId id) {
            this.id = id;
            return this;
        }

        public PurchaseOrderReconstructor orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public PurchaseOrderReconstructor supplierId(String supplierId) {
            this.supplierId = supplierId;
            return this;
        }

        public PurchaseOrderReconstructor supplierName(String supplierName) {
            this.supplierName = supplierName;
            return this;
        }

        public PurchaseOrderReconstructor items(List<PurchaseOrderItem> items) {
            this.items = items;
            return this;
        }

        public PurchaseOrderReconstructor totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public PurchaseOrderReconstructor status(PurchaseOrderStatus status) {
            this.status = status;
            return this;
        }

        public PurchaseOrderReconstructor orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public PurchaseOrderReconstructor expectedDeliveryDate(LocalDateTime expectedDeliveryDate) {
            this.expectedDeliveryDate = expectedDeliveryDate;
            return this;
        }

        public PurchaseOrderReconstructor actualDeliveryDate(LocalDateTime actualDeliveryDate) {
            this.actualDeliveryDate = actualDeliveryDate;
            return this;
        }

        public PurchaseOrderReconstructor deliveryLocation(String deliveryLocation) {
            this.deliveryLocation = deliveryLocation;
            return this;
        }

        public PurchaseOrderReconstructor createdBy(UserId createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public PurchaseOrderReconstructor approvedBy(UserId approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public PurchaseOrderReconstructor createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PurchaseOrderReconstructor updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PurchaseOrder reconstruct() {
            return new PurchaseOrder(id, orderNumber, supplierId, supplierName, items, totalAmount, status, orderDate, expectedDeliveryDate, actualDeliveryDate, deliveryLocation, createdBy, approvedBy, createdAt, updatedAt);
        }
    }
}