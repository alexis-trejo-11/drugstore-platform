package io.github.alexisTrejo11.drugstore.order.orders.application.commands.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.DeliveryMethod;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;

import java.util.List;

@Getter
public class CreatePickupOrderCommand extends CreateOrderCommand {
    @NotNull String storeID;
    @NotNull String storeName;
    @NotNull String storeAddress;

    public CreatePickupOrderCommand(
            UserID userID,
            DeliveryMethod deliveryMethod,
            String notes, List<@NotNull CreateOrderItemCommand> items,
            String storeID,
            String storeName,
            String storeAddress
    ) {
        super(userID, deliveryMethod, notes, items);
        this.storeID = storeID;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
    }
}