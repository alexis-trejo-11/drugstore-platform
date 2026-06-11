package io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response;

import io.github.alexisTrejo11.drugstore.order.external.address.model.DeliveryAddressResponse;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.DeliveryInfoQueryResult;

import java.time.LocalDateTime;

public record DeliveryInfoResponse(
        String trackingNumber,
        Integer deliveryAttempt,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime actualDeliveryDate,
        DeliveryAddressResponse deliveryAddress) {
    public static DeliveryInfoResponse from(DeliveryInfoQueryResult response) {
        if (response == null)
            return null;
        return new DeliveryInfoResponse(
                response.trackingNumber(),
                response.deliveryAttempt(),
                response.estimatedDeliveryDate(),
                response.actualDeliveryDate(),
                DeliveryAddressResponse.from(response.deliveryAddress()));
    }
}
