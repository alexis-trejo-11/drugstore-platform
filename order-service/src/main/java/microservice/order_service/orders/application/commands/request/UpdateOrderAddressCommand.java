package microservice.order_service.orders.application.commands.request;


import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

public record UpdateOrderAddressCommand(
        AddressID addressID,
        UserID userID,
        OrderID orderID
) {
}
