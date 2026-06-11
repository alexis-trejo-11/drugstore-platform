package io.github.alexisTrejo11.drugstore.order.orders.application.query.response;

import io.github.alexisTrejo11.drugstore.order.external.address.model.DeliveryAddress;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.DeliveryInfo;

import java.time.LocalDateTime;

public record DeliveryInfoQueryResult(
        String trackingNumber,
        Integer deliveryAttempt,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime actualDeliveryDate,
        DeliveryAddress deliveryAddress) {
    public static DeliveryInfoQueryResult from(DeliveryInfo deliveryInfo) {
        if (deliveryInfo == null)
            return null;

        return new DeliveryInfoQueryResult(
                deliveryInfo.getTrackingNumber(),
                deliveryInfo.getDeliveryAttempt(),
                deliveryInfo.getEstimatedDeliveryDate(),
                deliveryInfo.getActualDeliveryDate(),
                deliveryInfo.getAddress());
    }
}