package microservice.order_service.orders.infrastructure.api.dto.response;

import microservice.order_service.external.address.infrastructure.api.dto.DeliveryAddressResponse;
import microservice.order_service.orders.application.queries.response.DeliveryInfoQueryResult;

import java.time.LocalDateTime;

public record DeliveryInfoResponse(
        String trackingNumber,
        Integer deliveryAttempt,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime actualDeliveryDate,
        DeliveryAddressResponse deliveryAddress
) {
    public static DeliveryInfoResponse from(DeliveryInfoQueryResult response) {
        if (response == null) return null;
        return new DeliveryInfoResponse(
                response.trackingNumber(),
                response.deliveryAttempt(),
                response.estimatedDeliveryDate(),
                response.actualDeliveryDate(),
                DeliveryAddressResponse.from(response.deliveryAddress())
        );
    }
}
