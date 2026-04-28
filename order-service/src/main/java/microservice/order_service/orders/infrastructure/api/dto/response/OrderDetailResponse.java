package microservice.order_service.orders.infrastructure.api.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderDetailResponse(
        String id,
        DeliveryMethod deliveryMethod,
        OrderStatus status,
        String notes,
        String taxAmount,
        String totalAmount,

        DeliveryInfoResponse deliveryInfo,
        PickupInfoResponse pickupInfo,
        List<OrderItemResponse> items,
        UserResponse userResponse,
        String paymentID,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}

