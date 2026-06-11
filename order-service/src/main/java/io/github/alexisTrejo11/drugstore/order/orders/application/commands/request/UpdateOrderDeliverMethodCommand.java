package io.github.alexisTrejo11.drugstore.order.orders.application.commands.request;


import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.DeliveryMethod;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.DeliveryInfo;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.PickupInfo;

public record UpdateOrderDeliverMethodCommand(
        OrderID orderID,
        DeliveryMethod newMethod,
        DeliveryInfo deliveryInfo,
        PickupInfo pickupInfo
        ) {
}
