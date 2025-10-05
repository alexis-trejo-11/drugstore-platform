package microservice.order_service.infrastructure.api.controller.dto;


import lombok.Builder;
import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderDetailResponse(
        String orderId,
        String customerId,
        String status,
        BigDecimal totalAmount,
        String deliveryMethod,
        DeliveryAddressResponse deliveryAddress,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime estimatedDeliveryDate,
        String notes,
        List<OrderItemResponse> items,
        List<OrderStatusHistoryResponse> statusHistory
) {

}