package io.github.alexisTrejo11.drugstore.order.orders.application.commands.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.DeliveryMethod;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.AddressID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;

import java.util.List;

@Getter
public class CreateDeliveryOrderCommand extends CreateOrderCommand {
    @NotNull
    AddressID addressID;

    public CreateDeliveryOrderCommand(
            UserID userID,
            DeliveryMethod deliveryMethod,
            String notes, List<@NotNull CreateOrderItemCommand> items,
            AddressID addressID
    ) {
        super(userID, deliveryMethod, notes, items);
        this.addressID = addressID;
    }
}
