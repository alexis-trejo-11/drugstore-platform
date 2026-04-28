package microservice.order_service.orders.application.queries.response;

import lombok.Builder;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
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
    DeliveryMethod deliveryMethod,
    OrderStatus status,
    String notes,
    Money taxAmount,
    Money totalAmount,

    DeliveryInfoQueryResult deliveryInfo,
    PickupInfoQueryResult pickupInfo,
    List<OrderItemQueryResult> items,
    User user,
    PaymentID paymentID,

    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static OrderDetailResult from(Order order, User user) {
        if  (order == null) return null;

        var resultItems = order.getItems().stream()
                .map(OrderItemQueryResult::from)
                .toList();

        return OrderDetailResult.builder()
                .id(order.getId())
                .deliveryMethod(order.getDeliveryMethod())
                .status(order.getStatus())
                .notes(order.getNotes())
                .taxAmount(order.getTaxFee())
                .totalAmount(order.getTotalAmount())

                .deliveryInfo(order.getDeliveryInfo() != null ? DeliveryInfoQueryResult.from(order.getDeliveryInfo()) : null)
                .pickupInfo(order.getPickupInfo() != null ? PickupInfoQueryResult.from(order.getPickupInfo()) : null)
                .user(user)
                .items(resultItems)
                .paymentID(order.getPaymentID())

                .createdAt(order.getOrderTimestamps().getCreatedAt())
                .updatedAt(order.getOrderTimestamps().getUpdatedAt())
                .build();
    }
}
