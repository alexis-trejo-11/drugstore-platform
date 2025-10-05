package microservice.order_service.application.queries.response;

import lombok.Builder;
import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.OrderID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderQueryResult(
    OrderID orderId,
    BigDecimal totalAmount,
    String currency,
    OrderStatus status,
    LocalDateTime createdAt,
    DeliveryMethod deliveredMethod,
    LocalDateTime estimatedDeliveryDate,
    Integer totalItems
) {}
