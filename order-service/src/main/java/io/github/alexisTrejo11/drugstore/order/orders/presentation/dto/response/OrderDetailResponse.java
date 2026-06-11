package io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.DeliveryMethod;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.OrderStatus;
import io.github.alexisTrejo11.drugstore.order.external.user.infra.dto.UserResponse;

import java.time.LocalDateTime;
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

