package microservice.order_service.infrastructure.api.controller.dto;

import lombok.Builder;
import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderResponse(
        String orderId,
        String status,
        String deliveredMethod,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        LocalDateTime estimatedDeliveryDate
) {}
