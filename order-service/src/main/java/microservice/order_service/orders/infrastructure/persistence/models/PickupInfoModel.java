package microservice.order_service.orders.infrastructure.persistence.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.order_service.orders.domain.models.valueobjects.PickupInfo;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pickup_info")
public class PickupInfoModel {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "store_id", nullable = false)
    private String storeID;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_address", length = 255)
    private String storeAddress;

    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;

    @Column(name = "pickup_code", length = 6)
    private String pickupCode;

    @Column(name = "available_until")
    private LocalDateTime availableUntil;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private OrderModel order;

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

    public static PickupInfoModel from(PickupInfo pickupInfo) {
        if (pickupInfo == null) return null;

        return PickupInfoModel.builder()
                .id(pickupInfo.getId())
                .storeID(pickupInfo.getStoreID())
                .storeName(pickupInfo.getStoreName())
                .storeAddress(pickupInfo.getStoreAddress())
                .availableUntil(pickupInfo.getReadyForPickupAt())
                .pickedUpAt(pickupInfo.getPickedUpAt())
                .pickupCode(pickupInfo.getPickupCode())
                .build();
    }

    public PickupInfo toDomain() {
        return PickupInfo.builder()
                .id(this.id)
                .storeID(this.storeID)
                .storeName(this.storeName)
                .storeAddress(this.storeAddress)
                .readyForPickupAt(this.availableUntil)
                .pickedUpAt(this.pickedUpAt)
                .pickupCode(this.pickupCode)
                .build();
    }
}
