package io.github.alexisTrejo11.drugstore.order.orders.application.commands.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.DeliveryMethod;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@AllArgsConstructor
public abstract class CreateOrderCommand {
    @NotNull
    UserID userID;
    @NotNull
    DeliveryMethod deliveryMethod;
    @Length(max = 500) String notes;
    @NotEmpty
    List<@NotNull CreateOrderItemCommand> items;
}
