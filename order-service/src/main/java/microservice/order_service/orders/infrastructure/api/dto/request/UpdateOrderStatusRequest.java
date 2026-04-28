package microservice.order_service.orders.infrastructure.api.dto.request;

import microservice.order_service.orders.domain.models.enums.OrderStatus;

public record UpdateOrderStatusRequest(
        OrderStatus newStatus,
        String updatedBy
) {}