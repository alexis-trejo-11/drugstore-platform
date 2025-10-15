package microservice.order_service.orders.domain.models.valueobjects;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class PickupInfo{
    String storeID;
    String storeName;
    LocalDateTime readyForPickupAt;
    LocalDateTime pickedUpAt;
    String pickupCode;

    public PickupInfo(
        String storeID,
        String storeName,
        LocalDateTime readyForPickupAt,
        LocalDateTime pickedUpAt,
        String pickupCode
    ) {
        this.storeID = storeID;
        this.storeName = storeName;
        this.readyForPickupAt = readyForPickupAt;
        this.pickedUpAt = pickedUpAt;
        this.pickupCode = pickupCode;
    }

    public void ready() {
        this.readyForPickupAt = LocalDateTime.now();
    }

    public void complete() {
        this.pickedUpAt = LocalDateTime.now();
    }

    public static PickupInfo create(String storeID, String storeName, String pickupCode) {
        return new PickupInfo(storeID, storeName, null, null, pickupCode);
    }

    public Integer getDaysSinceReadyForPickup() {
        if (this.readyForPickupAt == null){
            return null;
        }

        return (int) Duration.between(this.readyForPickupAt, LocalDateTime.now()).toDays();
    }


}
