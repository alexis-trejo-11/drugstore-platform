package microservice.order_service.orders.infrastructure.persistence.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.order_service.external.address.infrastructure.persistence.Model.DeliveryAddressModel;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;
import microservice.order_service.orders.domain.models.enums.Currency;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.enums.OrderStatus;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method", nullable = false)
    private DeliveryMethod deliveryMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "tax_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxFee;

    @Column(name = "service_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal serviceFee;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserModel user;

    @OneToOne(mappedBy = "orderModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DeliveryInfoModel deliveryInfo;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PickupInfo pickupInfo;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemModel> items;

    @Column(name = "user_id", insertable = false, updatable = false, length = 36)
    private String userID;

    @Column(name = "payment_id", length = 36)
    private String paymentID;

    @Column(name = "estimated_delivery_date", length = 16)
    private String deliveryInfoID;

    @Column(name = "pickup_info_id", length = 36)
    private String pickupInfoID;

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

    public OrderModel(String id) {
        this.id = id;
    }
}
