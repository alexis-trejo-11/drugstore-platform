package microservice.order_service.orders.application.queries.response;

import lombok.Builder;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.infrastructure.api.dto.DeliveryAddressResponse;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.PaymentID;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderDetailResult(
    OrderID id,
    Money totalAmount,
    DeliveryMethod deliveryMethod,
    OrderStatus status,
    String notes,

    User user,
    List<OrderItemQueryResult> items,
    DeliveryAddress deliveryAddress,
    PaymentID paymentID,

    Integer daysSinceReadyForPickup,
    String deliveryTrackingNumber,
    Integer deliveryAttempt,

    Money shippingCost,
    Money taxAmount,

    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime estimatedDeliveryDate
) {
    public static OrderDetailResult from(Order order) {
        return OrderDetailResult.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .deliveryMethod(order.getDeliveryMethod())
                .status(order.getStatus())
                .notes(order.getNotes() != null ? order.getNotes() : "")

                .shippingCost(order.getShippingCost() != null ? order.getShippingCost() :  Money.zero())
                .taxAmount(order.getTaxAmount() != null ? order.getTaxAmount() : Money.zero())

                .daysSinceReadyForPickup(order.getDaysSinceReadyForPickup() != null ? order.getDaysSinceReadyForPickup() : null)
                .deliveryAttempt(order.getDeliveryAttempt() != null ? order.getDeliveryAttempt() : null)
                .deliveryTrackingNumber(order.getDeliveryTrackingNumber() != null ? order.getDeliveryTrackingNumber() : null)

                .user(order.getUser())
                .items(order.getItems().stream().map(OrderItemQueryResult::from).toList())
                .deliveryAddress(order.getDeliveryAddress())
                .paymentID(order.getPaymentID())

                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .estimatedDeliveryDate(order.getEstimatedDeliveryDate())
                .build();
    }
}
