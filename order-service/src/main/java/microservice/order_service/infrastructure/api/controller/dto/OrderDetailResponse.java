package microservice.order_service.infrastructure.api.controller.dto;


import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        String orderId,
        String customerId,
        OrderStatus status,
        BigDecimal totalAmount,
        DeliveryMethod deliveryMethod,
        DeliveryAddressResponse deliveryAddress,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime estimatedDeliveryDate,
        String notes,
        List<OrderItemResponse> items,
        List<OrderStatusHistoryResponse> statusHistory
) {}