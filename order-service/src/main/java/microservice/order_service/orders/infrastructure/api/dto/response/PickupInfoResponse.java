package microservice.order_service.orders.infrastructure.api.dto.response;

import microservice.order_service.orders.application.queries.response.PickupInfoQueryResult;

import java.time.LocalDateTime;

public record PickupInfoResponse(
        String storeID,
        String storeName,
        LocalDateTime readyForPickupAt,
        LocalDateTime pickedUpAt,
        Integer daysSinceReadyForPickup,
        String pickupCode
) {
    public static PickupInfoResponse from(PickupInfoQueryResult response) {
        return new PickupInfoResponse(
                response.storeID(),
                response.storeName(),
                response.readyForPickupAt(),
                response.pickedUpAt(),
                response.daysSinceReadyForPickup(),
                response.pickupCode()
        );
    }
}
