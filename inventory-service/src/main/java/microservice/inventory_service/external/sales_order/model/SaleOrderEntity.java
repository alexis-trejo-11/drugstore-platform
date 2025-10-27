package microservice.inventory_service.external.sales_order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sale_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderEntity {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method", nullable = false)
    private DeliveryMethod deliveryMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "notes", length = 500)
    private String notes;

    @OneToMany(mappedBy = "saleOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SaleOrderItemEntity> items;

    @Column(name = "user_id", insertable = false, updatable = false, length = 36)
    private String userId;

    @Column(name = "payment_id", length = 36)
    private String paymentId;

    @Column(name = "delivery_info_id", length = 36)
    private String deliveryInfoId;

    @Column(name = "pickup_info_id", length = 36)
    private String pickupInfoId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public SaleOrderEntity(String id) {
        this.id = id;
    }

    public void confirmPayment(String paymentId) {
        if (this.status != OrderStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("PurchaseOrder status must be PENDING to confirm payment.");
        }

        if (this.paymentId != null) {
            throw new IllegalStateException("Payment has already been confirmed for this order.");
        }

        this.paymentId = paymentId;
        this.status = OrderStatus.APPROVED;
    }

    public void fulfillOrder() {
        if (this.status != OrderStatus.APPROVED) {
            throw new IllegalStateException("PurchaseOrder status must be APPROVED to fulfill the order.");
        }

        this.status = OrderStatus.FULFILLED;
    }

    public void cancelOrder(String reason) {
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("PurchaseOrder is already cancelled.");
        }

        this.status = OrderStatus.CANCELLED;
        this.notes = reason;
    }


    public void readyToDelivery() {
        if (this.status != OrderStatus.APPROVED) {
            throw new IllegalStateException("PurchaseOrder status must be APPROVED to mark as ready for delivery.");
        }

        this.status = OrderStatus.READY_FOR_LEAVE;
    }
}
