package microservice.order_service.orders.infrastructure.api.dto;


import lombok.Builder;
import microservice.order_service.external.address.infrastructure.api.dto.DeliveryAddressResponse;
import microservice.order_service.external.users.infrastructure.api.dto.UserResponse;
import microservice.order_service.orders.application.queries.response.OrderDetailResult;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderDetailResponse(
        String orderId,
        String status,
        String totalAmount,
        String deliveryMethod,

        UserResponse userResponse,
        DeliveryAddressResponse deliveryAddress,
        List<OrderItemResponse> items,
        String paymentID,

        Integer daysSinceReadyForPickup,
        String deliveryTrackingNumber,
        Integer deliveryAttempt,

        String shippingCost,
        String taxCost,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime estimatedDeliveryDate
) {
}