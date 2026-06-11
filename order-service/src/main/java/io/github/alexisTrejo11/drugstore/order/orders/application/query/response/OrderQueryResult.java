package io.github.alexisTrejo11.drugstore.order.orders.application.query.response;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.Order;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.DeliveryMethod;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.OrderStatus;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;

import java.time.LocalDateTime;

@Builder
public record OrderQueryResult(
    OrderID id,
    OrderStatus status,
    DeliveryMethod deliveryMethod,
    Money totalAmount,
    Integer totalItems,
    UserID userID,
    LocalDateTime createdAt
) {

  public static OrderQueryResult toResult(Order order) {
    return OrderQueryResult.builder()
        .id(order.getId() != null ? order.getId() : null)
        .userID(order.getUserID() != null ? order.getUserID(): null)
        .status(order.getStatus() != null ? order.getStatus(): null)
        .totalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : null)
        .deliveryMethod(order.getDeliveryMethod() != null ? order.getDeliveryMethod() : null)
        .totalItems(order.getTotalItemsCount())
        .createdAt(order.getOrderTimestamps().getCreatedAt() != null ? order.getOrderTimestamps().getCreatedAt() : null)
        .build();
  }
}
