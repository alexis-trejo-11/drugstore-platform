package microservice.order_service.application.queries;

import microservice.order_service.domain.models.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderQuery(
    UUID orderId,
    UUID customerId,
    OrderStatus status,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean activeOnly
) {
    public static OrderQuery byId(UUID orderId) {
        return new OrderQuery(orderId, null, null, null, null, null);
    }

    public static OrderQuery byCustomerId(UUID customerId) {
        return new OrderQuery(null, customerId, null, null, null, null);
    }

    public static OrderQuery byStatus(OrderStatus status) {
        return new OrderQuery(null, null, status, null, null, null);
    }

    public static OrderQuery byCustomerAndStatus(UUID customerId, OrderStatus status) {
        return new OrderQuery(null, customerId, status, null, null, null);
    }

    public static OrderQuery byDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return new OrderQuery(null, null, null, startDate, endDate, null);
    }

    public static OrderQuery activeByCustomer(UUID customerId) {
        return new OrderQuery(null, customerId, null, null, null, true);
    }

    public static OrderQuery all() {
        return new OrderQuery(null, null, null, null, null, null);
    }
}
