package io.github.alexisTrejo11.drugstore.order.orders.application.commands.request;


import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.AddressID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;

public record UpdateOrderAddressCommand(
        AddressID addressID,
        UserID userID,
        OrderID orderID
) {
}
