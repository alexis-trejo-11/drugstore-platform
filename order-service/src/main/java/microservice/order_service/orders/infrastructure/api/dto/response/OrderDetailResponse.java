package microservice.order_service.orders.infrastructure.api.dto.response;


import lombok.Builder;
import microservice.order_service.external.address.infrastructure.api.dto.DeliveryAddressResponse;
import microservice.order_service.external.users.infrastructure.api.dto.UserResponse;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@Builder
public record OrderDetailResponse(
        String id,
        DeliveryMethod deliveryMethod,
        OrderStatus status,
        String notes,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        Currency currency,

        DeliveryInfoResponse deliveryInfo,
        PickupInfoResponse pickupInfo,
        List<OrderItemResponse> items,
        DeliveryAddressResponse deliveryAddress,
        UserResponse userResponse,
        String paymentID,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}

