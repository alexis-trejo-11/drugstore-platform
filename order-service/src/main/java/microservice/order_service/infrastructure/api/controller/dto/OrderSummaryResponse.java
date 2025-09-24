package microservice.order_service.infrastructure.api.controller.dto;


import microservice.order_service.domain.models.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        String orderId,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime orderDate,
        int itemCount,
        String deliveryMethod
) {}
