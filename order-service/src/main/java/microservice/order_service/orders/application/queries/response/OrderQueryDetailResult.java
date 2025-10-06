package microservice.order_service.orders.application.queries.response;

import lombok.Builder;
import microservice.order_service.external.address.infrastructure.api.dto.DeliveryAddressResponse;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderQueryDetailResult(
    OrderID orderId,
    User user,
    List<OrderItemQueryResult> items,
    BigDecimal totalAmount,
    String currency,
    DeliveryMethod deliveryMethod,
    DeliveryAddressResponse deliveryAddress,
    OrderStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime estimatedDeliveryDate,
    String notes
) {
    public static OrderQueryDetailResult from(Order order) {
        return OrderQueryDetailResult.builder()
                .orderId(order.getId())
                .user(order.getUser())
                .items(order.getItems().stream().map(OrderItemQueryResult::from).toList())
                .totalAmount(order.getTotalAmount().amount())
                .currency(order.getTotalAmount().currency().getDisplayName())
                .deliveryMethod(order.getDeliveryMethod())
                .deliveryAddress(DeliveryAddressResponse.from(order.getDeliveryAddress()))
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .estimatedDeliveryDate(order.getEstimatedDeliveryDate())
                .notes(order.getNotes())
                .build();
    }
}
