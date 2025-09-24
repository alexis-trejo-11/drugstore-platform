package microservice.order_service.infrastructure.api.controller.dto;

import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        String orderId,
        String customerId,
        OrderStatus status,
        BigDecimal totalAmount,
        DeliveryMethod deliveryMethod,
        LocalDateTime createdAt,
        LocalDateTime estimatedDeliveryDate
) {}
