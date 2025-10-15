package microservice.order_service.orders.infrastructure.persistence.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.order_service.external.address.infrastructure.persistence.Model.DeliveryAddressModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryInfoModel {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "delivery_attempt", nullable = false)
    private Integer deliveryAttempt;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @Column(name = "receiver_name", length = 100)
    private String receiverName;

    @Column(name = "delivery_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryCost;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
    private OrderModel orderModel;

    @ManyToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
    private DeliveryAddressModel deliveryAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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
}



