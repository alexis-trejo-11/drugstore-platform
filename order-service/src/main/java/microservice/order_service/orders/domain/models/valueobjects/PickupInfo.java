package microservice.order_service.orders.domain.models.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupInfo{
    String id;
    String storeID;
    String storeName;
    String storeAddress;
    LocalDateTime readyForPickupAt;
    LocalDateTime pickedUpAt;
    String pickupCode;

    public void ready() {
        this.readyForPickupAt = LocalDateTime.now();
    }

    public void complete() {
        this.pickedUpAt = LocalDateTime.now();
    }

    public static PickupInfo create(String storeID, String storeName, String storeAddress ,String pickupCode) {
        return new PickupInfo(
                UUID.randomUUID().toString(),
                storeID,
                storeName,
                storeAddress,
                null,
                null,
                pickupCode);
    }

    public Integer getDaysSinceReadyForPickup() {
        if (this.readyForPickupAt == null){
            return null;
        }

        return (int) Duration.between(this.readyForPickupAt, LocalDateTime.now()).toDays();
    }


}
