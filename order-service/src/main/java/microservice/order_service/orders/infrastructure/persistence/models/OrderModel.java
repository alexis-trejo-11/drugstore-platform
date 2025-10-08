package microservice.order_service.orders.infrastructure.persistence.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.order_service.external.address.infrastructure.persistence.Model.DeliveryAddressModel;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "delivery_method", nullable = false, length = 20)
    private String deliveryMethod;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "notes", length = 500)
    private String notes;

    // Numeric Values
    @Column(name = "shipping_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost;

    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    // Shipping
    @Column(name = "delivery_tracking_number", length = 100)
    private String deliveryTrackingNumber;

    @Column(name = "delivery_attempt", nullable = false)
    private Integer deliveryAttempt;

    @Column(name = "days_since_ready_for_pickup", nullable = false)
    private Integer daysSinceReadyForPickup;

    // Relationships
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserModel user;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private DeliveryAddressModel deliveryAddressModel;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemModel> items;

    @Column(name = "payment_id", length = 36)
    private String paymentID;

    // TimeStamps
    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
