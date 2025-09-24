package microservice.order_service.infrastructure.api.controller.dto;

import microservice.order_service.domain.models.enums.OrderStatus;

public record UpdateOrderStatusRequest(
        OrderStatus newStatus,
        String updatedBy
) {}