package microservice.order_service.orders.application.queries.response;

import microservice.order_service.orders.domain.models.valueobjects.DeliveryInfo;

import java.time.LocalDateTime;

public record DeliveryInfoQueryResult(
        String trackingNumber,
        Integer deliveryAttempt,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime actualDeliveryDate
) {
    public static DeliveryInfoQueryResult from(DeliveryInfo deliveryInfo) {
        if (deliveryInfo == null) return null;

        return new DeliveryInfoQueryResult(
                deliveryInfo.getTrackingNumber(),
                deliveryInfo.getDeliveryAttempt(),
                deliveryInfo.getEstimatedDeliveryDate(),
                deliveryInfo.getActualDeliveryDate()
        );
    }
}