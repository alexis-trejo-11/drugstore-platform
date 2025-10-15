package microservice.order_service.orders.application.queries.response;

import microservice.order_service.orders.domain.models.valueobjects.PickupInfo;

import java.time.LocalDateTime;

public record PickupInfoQueryResult(
        String storeID,
        String storeName,
        LocalDateTime readyForPickupAt,
        LocalDateTime pickedUpAt,
        Integer daysSinceReadyForPickup,
        String pickupCode
) {
    public static PickupInfoQueryResult from(PickupInfo pickupInfo) {
        if (pickupInfo == null) return null;

        return new PickupInfoQueryResult(
                pickupInfo.getStoreID(),
                pickupInfo.getStoreName(),
                pickupInfo.getReadyForPickupAt(),
                pickupInfo.getPickedUpAt(),
                pickupInfo.getDaysSinceReadyForPickup(),
                pickupInfo.getPickupCode()
        );
    }
}
