package microservice.order_service.orders.infrastructure.api.controller.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderResponse(
        String orderId,
        String userID,
        String status,
        String totalAmount,
        Integer totalItems,
        String deliveryMethod
) {}
