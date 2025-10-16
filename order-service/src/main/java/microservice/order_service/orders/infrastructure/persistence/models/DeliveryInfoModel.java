package microservice.order_service.orders.infrastructure.persistence.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.order_service.external.address.infrastructure.persistence.Model.DeliveryAddressModel;
import microservice.order_service.orders.domain.models.enums.Currency;
import microservice.order_service.orders.domain.models.valueobjects.DeliveryInfo;
import microservice.order_service.orders.domain.models.valueobjects.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_info")
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

    @Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", length = 3, nullable = false)
    private Currency currency;

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


    public static DeliveryInfoModel from(DeliveryInfo deliveryInfo) {
        if (deliveryInfo == null) return null;

        return DeliveryInfoModel.builder()
                .id(deliveryInfo.getId())
                .actualDeliveryDate(deliveryInfo.getActualDeliveryDate())
                .estimatedDeliveryDate(deliveryInfo.getEstimatedDeliveryDate())
                .trackingNumber(deliveryInfo.getTrackingNumber())
                .deliveryAttempt(deliveryInfo.getDeliveryAttempt())
                .receiverName(deliveryInfo.getReceiverName())
                .deliveryFee(deliveryInfo.getDeliveryFee() != null ? deliveryInfo.getDeliveryFee().amount() : null)
                .currency(deliveryInfo.getDeliveryFee() != null ? Currency.fromCode(deliveryInfo.getDeliveryFee().currency().getCurrencyCode()) : null)
                .deliveryAddress(new DeliveryAddressModel(deliveryInfo.getAddressID().value()))
                .build();
    }

    public DeliveryInfo toDomain() {
        return new DeliveryInfo(
                this.id,
                this.trackingNumber,
                this.deliveryAttempt,
                this.estimatedDeliveryDate,
                this.actualDeliveryDate,
                this.deliveryFee != null ? Money.of(this.deliveryFee, this.currency != null ? java.util.Currency.getInstance(this.currency.getCode()) : null) : null
        );
    }
}



