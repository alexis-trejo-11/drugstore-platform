package microservice.order_service.orders.application.commands.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.hibernate.validator.constraints.Length;

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