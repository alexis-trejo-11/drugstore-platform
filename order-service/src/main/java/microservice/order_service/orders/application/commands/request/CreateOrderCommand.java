package microservice.order_service.orders.application.commands.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
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
