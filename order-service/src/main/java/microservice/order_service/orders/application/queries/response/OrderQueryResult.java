package microservice.order_service.orders.application.queries.response;

import lombok.Builder;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderQueryResult(
    OrderID id,
    UserID userID,
    BigDecimal totalAmount,
    String currency,
    OrderStatus status,
    DeliveryMethod deliveryMethod,
    Integer totalItems
) {}
