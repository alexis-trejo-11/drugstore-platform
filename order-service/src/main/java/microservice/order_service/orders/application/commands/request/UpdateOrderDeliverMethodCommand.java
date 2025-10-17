package microservice.order_service.orders.application.commands.request;


import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.DeliveryInfo;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.PickupInfo;

import java.time.LocalDateTime;

public record UpdateOrderDeliverMethodCommand(
        OrderID orderID,
        DeliveryMethod newMethod,
        DeliveryInfo deliveryInfo,
        PickupInfo pickupInfo
        ) {
}
