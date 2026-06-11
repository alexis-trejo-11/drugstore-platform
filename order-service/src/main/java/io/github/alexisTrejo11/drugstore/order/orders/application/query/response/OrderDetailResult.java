package io.github.alexisTrejo11.drugstore.order.orders.application.query.response;

import lombok.Builder;

import io.github.alexisTrejo11.drugstore.order.orders.domain.models.Order;
import io.github.alexisTrejo11.drugstore.order.external.user.model.User;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.DeliveryMethod;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.OrderStatus;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.PaymentID;

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
