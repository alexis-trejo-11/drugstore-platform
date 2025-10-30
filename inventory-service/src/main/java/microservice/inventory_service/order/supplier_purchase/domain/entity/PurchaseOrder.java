package microservice.inventory_service.order.supplier_purchase.domain.entity;

import lombok.Getter;
import microservice.inventory_service.order.sales.core.domain.exception.OrderStatusValidationException;
import microservice.inventory_service.order.sales.core.domain.exception.PurchaseOrderValidationException;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.*;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.*;

import microservice.inventory_service.shared.domain.order.BaseDomainEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class PurchaseOrder extends BaseDomainEntity<PurchaseOrderId> {
    private String supplierId;
    private String supplierName;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String deliveryLocation;
    private UserId createdBy;
    private UserId approvedBy;
    private List<PurchaseOrderItem> items;
    private static final Logger log = LoggerFactory.getLogger(PurchaseOrder.class);

    private PurchaseOrder(PurchaseOrderId id, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, Integer version) {
        super(id, createdAt, updatedAt, deletedAt, version);
    }
    private PurchaseOrder(PurchaseOrderId id) {
        super(id);
    }

    public static PurchaseOrder reconstruct(ReconstructPurchaseOrderParams params) {
        if (params == null) return null;

        PurchaseOrder purchaseOrder = new PurchaseOrder(params.id(), params.createdAt(), params.updatedAt(), params.deletedAt(), params.version());
        purchaseOrder.supplierId = params.supplierId();
        purchaseOrder.supplierName = params.supplierName();
        purchaseOrder.status = params.status();
        purchaseOrder.orderDate = params.orderDate();
        purchaseOrder.expectedDeliveryDate = params.expectedDeliveryDate();
        purchaseOrder.actualDeliveryDate = params.actualDeliveryDate();
        purchaseOrder.deliveryLocation = params.deliveryLocation();
        purchaseOrder.createdBy = params.createdBy();
        purchaseOrder.approvedBy = params.approvedBy();
        purchaseOrder.items = new ArrayList<>(params.items());
        return purchaseOrder;
    }

    public static PurchaseOrder create(CreatePurchaseOrderParams params) {
        PurchaseOrder purchaseOrder = new PurchaseOrder(params.id());
        purchaseOrder.supplierId = params.supplierId();
        purchaseOrder.supplierName = params.supplierName();
        purchaseOrder.items = new ArrayList<>(params.items());
        purchaseOrder.expectedDeliveryDate = params.expectedDeliveryDate();
        purchaseOrder.deliveryLocation = params.deliveryLocation();
        purchaseOrder.createdBy = params.createdBy();
        purchaseOrder.status = OrderStatus.PENDING_APPROVAL;
        purchaseOrder.orderDate = LocalDateTime.now();
        return purchaseOrder;
    }

    public void approve(UserId approvedBy) {
        log.info("Approving PurchaseOrder with ID: {}", this.id);
        if (this.status != OrderStatus.PENDING_APPROVAL) {
            throw new OrderStatusValidationException("Can only approve orders pending approval");
        }

        this.status = OrderStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.updatedAt = LocalDateTime.now();
        log.info("PurchaseOrder with ID: {} approved successfully", this.id);
    }

    public void supplierSending() {
        log.info("Marking PurchaseOrder with ID: {} as SENT", this.id);
        if (this.status != OrderStatus.APPROVED) {
            throw new OrderStatusValidationException("PurchaseOrder must be approved before sending");
        }

        this.status = OrderStatus.SENT;
        this.updatedAt = LocalDateTime.now();
        log.info("PurchaseOrder with ID: {} marked as SENT successfully", this.id);
    }

    public void receiveItems(List<ReceivedItem> receivedItems, LocalDateTime deliveryDate) {
        log.info("Receiving items for PurchaseOrder with ID: {}", this.id);
        if (this.status != OrderStatus.SENT && this.status != OrderStatus.PARTIALLY_RECEIVED) {
            throw new PurchaseOrderValidationException("Invalid status for receiving items");
        }
        if (deliveryDate.isBefore(this.orderDate)) {
            throw new PurchaseOrderValidationException("Delivery date cannot be before order date");
        }
        if (receivedItems == null || receivedItems.isEmpty()) {
            throw new PurchaseOrderValidationException("Received items cannot be null or empty");
        }

        log.info("Processing received items for PurchaseOrder with ID: {}", this.id);
        for (ReceivedItem receivedItem : receivedItems) {
            PurchaseOrderItem purchaseOrderItem = findItemById(receivedItem.getItemId());
            purchaseOrderItem.receiveQuantity(receivedItem.getReceivedQuantity());
            purchaseOrderItem.assignBatchNumber(receivedItem.getBatchNumber());
        }

        this.actualDeliveryDate = deliveryDate;
        updateStatusBasedOnReceivedItems();
        this.updatedAt = LocalDateTime.now();
        log.info("Items received for PurchaseOrder with ID: {} successfully", this.id);
    }

    public void cancel() {
        log.info("Cancelling PurchaseOrder with ID: {}", this.id);
        if (this.status == OrderStatus.RECEIVED || this.status == OrderStatus.CANCELLED) {
            throw new OrderStatusValidationException("Cannot cancel order in current status");
        }

        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
        log.info("PurchaseOrder with ID: {} cancelled successfully", this.id);
    }

    public void reject() {
        log.info("Rejecting PurchaseOrder with ID: {}", this.id);
        if (this.status != OrderStatus.PENDING_APPROVAL) {
            throw new OrderStatusValidationException("Can only reject orders pending approval");
        }

        this.status = OrderStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
        log.info("PurchaseOrder with ID: {} rejected successfully", this.id);
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
                .orElseThrow(() -> new PurchaseOrderValidationException("PurchaseOrder item not found"));
    }

    public void validateHardDelete() {
        if (this.status != OrderStatus.CANCELLED && this.status != OrderStatus.REJECTED) {
            throw new OrderStatusValidationException("Can only hard validateHardDelete orders that are cancelled or rejected");
        }
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        log.info("PurchaseOrder with ID: {} soft deleted at {}", this.id, this.deletedAt);
    }

    public void validateUpdate() {
        log.info("Validating update for PurchaseOrder with status: {}", this.status);
        if (this.status != OrderStatus.DRAFT && this.status != OrderStatus.PENDING_APPROVAL) {
            throw new OrderStatusValidationException("Only PurchaseOrders in DRAFT or PENDING_APPROVAL status can be updated.");
        }

        log.info("PurchaseOrder update validated successfully.");
    }

    public List<PurchaseOrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}