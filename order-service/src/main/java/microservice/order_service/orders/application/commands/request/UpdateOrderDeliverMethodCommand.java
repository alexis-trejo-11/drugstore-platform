package microservice.order_service.orders.application.commands.request;


import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;

public record UpdateOrderDeliverMethodCommand(OrderID orderID, DeliveryMethod newMethod, AddressID addressID) {
}
