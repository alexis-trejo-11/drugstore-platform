package io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response;

import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.PickupInfoQueryResult;

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
        if (response == null) return null;
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
