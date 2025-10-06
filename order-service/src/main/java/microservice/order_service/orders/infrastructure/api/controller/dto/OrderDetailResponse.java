package microservice.order_service.orders.infrastructure.api.controller.dto;


import lombok.Builder;
import microservice.order_service.external.address.infrastructure.api.dto.DeliveryAddressResponse;
import microservice.order_service.external.users.infrastructure.api.dto.UserResponse;
import microservice.order_service.orders.application.queries.response.OrderQueryDetailResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderDetailResponse(
        String orderId,
        UserResponse userResponse,
        String status,
        BigDecimal totalAmount,
        String deliveryMethod,
        DeliveryAddressResponse deliveryAddress,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime estimatedDeliveryDate,
        String notes,
        List<OrderItemResponse> items
        //List<OrderStatusHistoryResponse> statusHistory
) {

    public static OrderDetailResponse from(OrderQueryDetailResult result) {
        return OrderDetailResponse.builder()
                .orderId(result.orderId() != null ?  result.orderId().value() : null)
                // TODO: Add UserResponse mapping
                //.(result.userID() != null ? result.userID().value() : null)
                .status(result.status() != null ? result.status().name() : null)
                .totalAmount(result.totalAmount())
                .deliveryMethod(result.deliveryMethod() != null ? result.deliveryMethod().name() : null)
                .deliveryAddress(result.deliveryAddress())
                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .estimatedDeliveryDate(result.estimatedDeliveryDate())
                .notes(result.notes())
                //.items(result.items())
                .build();
    }
}