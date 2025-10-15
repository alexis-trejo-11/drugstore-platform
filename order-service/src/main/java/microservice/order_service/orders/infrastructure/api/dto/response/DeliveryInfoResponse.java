package microservice.order_service.orders.infrastructure.api.dto.response;

import microservice.order_service.orders.application.queries.response.DeliveryInfoQueryResult;

import java.time.LocalDateTime;

public record DeliveryInfoResponse(
        String trackingNumber,
        Integer deliveryAttempt,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime actualDeliveryDate
) {
    public static DeliveryInfoResponse from(DeliveryInfoQueryResult response) {
        return new DeliveryInfoResponse(
                response.trackingNumber(),
                response.deliveryAttempt(),
                response.estimatedDeliveryDate(),
                response.actualDeliveryDate()
        );
    }
}
